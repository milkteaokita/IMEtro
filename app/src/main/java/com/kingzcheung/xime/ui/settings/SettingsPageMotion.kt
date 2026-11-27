package com.kingzcheung.xime.ui.settings

import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kingzcheung.xime.ui.metro.LocalMetroUiState
import com.kingzcheung.xime.ui.metro.MetroTurnstileDirection
import com.kingzcheung.xime.ui.metro.MetroTurnstileMotion
import com.kingzcheung.xime.ui.metro.MetroTurnstileStage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * 所有设置页共用的层级切换：Zune HD / Windows Phone 3D Turnstile 轉場，並連動動畫倍率。
 */
internal object SettingsPageMotion {
    val enter: EnterTransition
        @Composable get() = MetroTurnstileMotion.enter(LocalMetroUiState.current.animationScale)
    val exit: ExitTransition
        @Composable get() = MetroTurnstileMotion.exit(LocalMetroUiState.current.animationScale)
    val popEnter: EnterTransition
        @Composable get() = MetroTurnstileMotion.popEnter(LocalMetroUiState.current.animationScale)
    val popExit: ExitTransition
        @Composable get() = MetroTurnstileMotion.popExit(LocalMetroUiState.current.animationScale)

    val push: ContentTransform
        @Composable get() = (enter togetherWith exit).apply { targetContentZIndex = 1f }
    val pop: ContentTransform
        @Composable get() = (popEnter togetherWith popExit).apply { targetContentZIndex = -1f }
    val none: ContentTransform get() = EnterTransition.None togetherWith ExitTransition.None
}

/** 系统“移除动画”/动画时长为 0 时返回 false。 */
@Composable
internal fun rememberSettingsMotionEnabled(): Boolean {
    val resolver = LocalContext.current.contentResolver
    val metroScale = LocalMetroUiState.current.animationScale
    return remember(resolver, metroScale) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f && metroScale > 0.05f
    }
}

/**
 * 页内子页（同一路由里用状态切换的二级页）走与 NavHost 相同的推入/退出与预测性返回。
 * [page] 为 null 表示父页；[onBack] 负责把它置回 null。
 * [handleBack] 为 false 时返回手势交给外层 NavHost：从首页直接进的子页返回首页，预览里露出的也是首页，
 * 不能先露出本页的父页（GitHub #127）。
 */
@Composable
internal fun <T> SettingsSubPages(
    page: T?,
    onBack: () -> Unit,
    handleBack: Boolean = true,
    content: @Composable (T?) -> Unit,
) {
    val motion = rememberSettingsMotionEnabled()
    val state = remember { SeekableTransitionState(page) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(page, motion) {
        if (state.currentState == page && state.targetState == page) return@LaunchedEffect
        if (motion) state.animateTo(page) else state.snapTo(page)
    }
    if (motion) {
        PredictiveBackHandler(enabled = handleBack && page != null) { events ->
            val current = page
            try {
                events.collect { state.seekTo(it.progress, targetState = null) }
                onBack()
            } catch (e: CancellationException) {
                scope.launch { state.animateTo(current) }
                throw e
            }
        }
    } else {
        BackHandler(enabled = handleBack && page != null, onBack = onBack)
    }
    val animScale = LocalMetroUiState.current.animationScale
    val direction = remember { MetroTurnstileDirection() }
    rememberTransition(state).AnimatedContent(
        modifier = Modifier.fillMaxSize().background(LocalMetroUiState.current.background),
        transitionSpec = {
            direction.isPop = targetState == null
            subPageTransform(motion, animScale)
        },
        contentKey = { it },
    ) { target ->
        MetroTurnstileStage(
            transition = transition,
            isPopProvider = { direction.isPop },
            motionEnabled = motion,
        ) {
            content(target)
        }
    }
}

private fun <T> AnimatedContentTransitionScope<T?>.subPageTransform(motion: Boolean, animScale: Float): ContentTransform = when {
    !motion -> SettingsPageMotion.none
    targetState == null -> (MetroTurnstileMotion.popEnter(animScale) togetherWith MetroTurnstileMotion.popExit(animScale)).apply { targetContentZIndex = -1f }
    else -> (MetroTurnstileMotion.enter(animScale) togetherWith MetroTurnstileMotion.exit(animScale)).apply { targetContentZIndex = 1f }
}
