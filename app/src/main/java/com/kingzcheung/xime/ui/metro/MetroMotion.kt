package com.kingzcheung.xime.ui.metro

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt

/** Removes Material ripple globally inside XimeTheme so touch feedback uses Metro 3D Tilt. */
object NoRippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        object : Modifier.Node(), DrawModifierNode {
            override fun ContentDrawScope.draw() {
                drawContent()
            }
        }

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}

/**
 * Windows Phone / Zune HD 3D physical surface tilt on touch.
 * Observes [PointerEventPass.Initial] without consuming pointer events so scrolling and
 * clickable handlers remain completely unaffected.
 */
fun Modifier.metroTilt(
    maxTiltDegrees: Float = 5f,
    pressScale: Float = 0.97f,
    enabled: Boolean = true,
): Modifier = composed {
    val animScale = LocalMetroUiState.current.animationScale
    if (!enabled || animScale <= 0.05f) return@composed this

    var size by remember { mutableStateOf(IntSize.Zero) }
    val rotX = remember { Animatable(0f) }
    val rotY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val touchSlop = LocalViewConfiguration.current.touchSlop

    fun releaseTilt() {
        val dur = (160f * animScale).roundToInt().coerceIn(50, 260)
        scope.launch { rotX.animateTo(0f, tween(dur)) }
        scope.launch { rotY.animateTo(0f, tween(dur)) }
        scope.launch { scale.animateTo(1f, tween(dur)) }
    }

    this
        .onSizeChanged { size = it }
        .pointerInput(enabled, animScale, maxTiltDegrees, pressScale) {
            awaitPointerEventScope {
                while (true) {
                    val downEvent = awaitPointerEvent(PointerEventPass.Initial)
                    val down = downEvent.changes.firstOrNull { it.pressed && !it.previousPressed } ?: continue
                    val w = size.width.coerceAtLeast(1).toFloat()
                    val h = size.height.coerceAtLeast(1).toFloat()
                    val nx = ((down.position.x / w) - 0.5f) * 2f
                    val ny = ((down.position.y / h) - 0.5f) * 2f
                    val targetRotY = nx.coerceIn(-1f, 1f) * maxTiltDegrees
                    val targetRotX = -ny.coerceIn(-1f, 1f) * maxTiltDegrees
                    val targetScale = if (abs(nx) < 0.35f && abs(ny) < 0.35f) pressScale else 0.985f
                    val downDur = (75f * animScale).roundToInt().coerceIn(25, 130)
                    scope.launch { rotX.animateTo(targetRotX, tween(downDur)) }
                    scope.launch { rotY.animateTo(targetRotY, tween(downDur)) }
                    scope.launch { scale.animateTo(targetScale, tween(downDur)) }

                    val startPos = down.position
                    val pointerId = down.id
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                        if (!change.pressed) break
                        val dist = hypot(change.position.x - startPos.x, change.position.y - startPos.y)
                        if (dist > touchSlop) break
                    }
                    releaseTilt()
                }
            }
        }
        .graphicsLayer {
            cameraDistance = 16f * density
            rotationX = rotX.value
            rotationY = rotY.value
            scaleX = scale.value
            scaleY = scale.value
        }
}

/**
 * Choreographed Zune HD / Windows Phone 3D Turnstile motion curves matching
 * `metro-home/core/metro-ui/MetroTurnstileAnimator.kt`.
 */
object MetroTurnstileMotion {
    val EnterEasing = CubicBezierEasing(0.08f, 0.92f, 0.16f, 1.0f)
    val ExitEasing = CubicBezierEasing(0.32f, 0.0f, 0.78f, 0.35f)

    fun exitDurationMs(animScale: Float): Int = (180f * animScale).roundToInt().coerceIn(45, 320)
    fun enterDelayMs(animScale: Float): Int = (100f * animScale).roundToInt().coerceIn(25, 180)
    fun enterDurationMs(animScale: Float): Int = (300f * animScale).roundToInt().coerceIn(75, 500)
    fun popEnterDurationMs(animScale: Float): Int = (280f * animScale).roundToInt().coerceIn(70, 460)
    fun enterFadeMs(animScale: Float): Int = (150f * animScale).roundToInt().coerceIn(40, 260)
    fun popEnterFadeMs(animScale: Float): Int = (140f * animScale).roundToInt().coerceIn(40, 240)

    fun enter(animScale: Float): EnterTransition {
        if (animScale <= 0.05f) return EnterTransition.None
        return fadeIn(
            tween(
                durationMillis = enterFadeMs(animScale),
                delayMillis = enterDelayMs(animScale),
                easing = LinearOutSlowInEasing,
            )
        )
    }

    fun exit(animScale: Float): ExitTransition {
        if (animScale <= 0.05f) return ExitTransition.None
        return fadeOut(tween(durationMillis = exitDurationMs(animScale), easing = ExitEasing))
    }

    fun popEnter(animScale: Float): EnterTransition {
        if (animScale <= 0.05f) return EnterTransition.None
        return fadeIn(
            tween(
                durationMillis = popEnterFadeMs(animScale),
                delayMillis = enterDelayMs(animScale),
                easing = LinearOutSlowInEasing,
            )
        )
    }

    fun popExit(animScale: Float): ExitTransition {
        if (animScale <= 0.05f) return ExitTransition.None
        return fadeOut(tween(durationMillis = exitDurationMs(animScale), easing = ExitEasing))
    }
}

/** Tracks whether the active navigation transition is a forward push (false) or backward pop (true). */
class MetroTurnstileDirection {
    var isPop: Boolean = false
}

/**
 * Authentic Windows Phone 7 / 8.1 3D Turnstile page-flip stage driven directly by
 * [androidx.compose.animation.AnimatedContentScope.transition] (`PreEnter` / `Visible` / `PostExit`).
 * Hinges on the left edge (`TransformOrigin(0f, 0.5f)`):
 * - Forward push: outgoing swings `0° -> -74°`, incoming swings `+74° -> 0°`.
 * - Backward pop: outgoing swings `0° -> +74°`, returning parent swings `-74° -> 0°`.
 */
@Composable
fun MetroTurnstileStage(
    transition: Transition<EnterExitState>,
    isPopProvider: () -> Boolean,
    motionEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val animScale = LocalMetroUiState.current.animationScale
    if (!motionEnabled || animScale <= 0.05f) {
        Box(modifier.fillMaxSize()) { content() }
        return
    }

    val segmentIsPop = remember(transition.currentState, transition.targetState) { isPopProvider() }
    val exitMs = MetroTurnstileMotion.exitDurationMs(animScale)
    val enterDelayMs = MetroTurnstileMotion.enterDelayMs(animScale)
    val enterRotMs = MetroTurnstileMotion.enterDurationMs(animScale)
    val popEnterRotMs = MetroTurnstileMotion.popEnterDurationMs(animScale)

    val rotationY by transition.animateFloat(
        transitionSpec = {
            when {
                EnterExitState.PreEnter isTransitioningTo EnterExitState.Visible -> tween(
                    durationMillis = if (segmentIsPop) popEnterRotMs else enterRotMs,
                    delayMillis = enterDelayMs,
                    easing = MetroTurnstileMotion.EnterEasing,
                )
                EnterExitState.Visible isTransitioningTo EnterExitState.PostExit -> tween(
                    durationMillis = exitMs,
                    easing = MetroTurnstileMotion.ExitEasing,
                )
                else -> tween(
                    durationMillis = exitMs,
                    easing = MetroTurnstileMotion.EnterEasing,
                )
            }
        },
        label = "metroTurnstileRotationY",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> if (segmentIsPop) -74f else 74f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> if (segmentIsPop) 74f else -74f
        }
    }

    val translationXDp by transition.animateFloat(
        transitionSpec = {
            when {
                EnterExitState.PreEnter isTransitioningTo EnterExitState.Visible -> tween(
                    durationMillis = if (segmentIsPop) popEnterRotMs else enterRotMs,
                    delayMillis = enterDelayMs,
                    easing = MetroTurnstileMotion.EnterEasing,
                )
                EnterExitState.Visible isTransitioningTo EnterExitState.PostExit -> tween(
                    durationMillis = exitMs,
                    easing = MetroTurnstileMotion.ExitEasing,
                )
                else -> tween(
                    durationMillis = exitMs,
                    easing = MetroTurnstileMotion.EnterEasing,
                )
            }
        },
        label = "metroTurnstileTranslationX",
    ) { state ->
        when (state) {
            EnterExitState.PreEnter -> if (segmentIsPop) -20f else 28f
            EnterExitState.Visible -> 0f
            EnterExitState.PostExit -> if (segmentIsPop) 28f else -20f
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .graphicsLayer {
                cameraDistance = 24f * density
                transformOrigin = TransformOrigin(0f, 0.5f)
                this.rotationY = rotationY
                translationX = translationXDp * density
                clip = false
            }
    ) {
        content()
    }
}

/** Plays a staggered 3D Y-axis Turnstile swing when a page item first appears. */
fun Modifier.metroTurnstileItem(index: Int): Modifier = composed {
    val animScale = LocalMetroUiState.current.animationScale
    if (animScale <= 0.05f) return@composed this

    val progress = remember { Animatable(0f) }
    LaunchedEffect(animScale) {
        val stagger = ((index.coerceIn(0, 10)) * 22L * animScale).toLong()
        if (stagger > 0L) delay(stagger)
        val dur = (360f * animScale).roundToInt().coerceIn(80, 600)
        progress.animateTo(1f, tween(dur, easing = MetroTurnstileMotion.EnterEasing))
    }

    this.graphicsLayer {
        val p = progress.value
        cameraDistance = 24f * density
        transformOrigin = TransformOrigin(0f, 0.5f)
        rotationY = (1f - p) * 52f
        translationX = (1f - p) * 26f * density
        alpha = p
    }
}
