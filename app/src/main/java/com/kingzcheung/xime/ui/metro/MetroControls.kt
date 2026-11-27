package com.kingzcheung.xime.ui.metro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kingzcheung.xime.ui.i18n.Text
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Windows Phone 7 / 8.1 / 10 Mobile toggle switch:
 * status text ("開啟"/"關閉") on the left, 54dp × 24dp sharp outlined track with
 * accent fill when checked and a solid sliding rectangular thumb.
 */
@Composable
fun MetroToggleSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showStatusLabel: Boolean = true,
) {
    val metro = LocalMetroUiState.current
    val thumbProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween((140f * metro.animationScale).roundToInt().coerceAtLeast(0)),
        label = "metroSwitchThumb",
    )
    val trackFill by animateColorAsState(
        targetValue = if (checked) metro.accent else Color.Transparent,
        animationSpec = tween((120f * metro.animationScale).roundToInt().coerceAtLeast(0)),
        label = "metroSwitchFill",
    )
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .then(
                if (onCheckedChange != null) {
                    Modifier
                        .then(if (enabled) Modifier.metroTilt(4f, 0.98f) else Modifier)
                        .toggleable(
                            value = checked,
                            enabled = enabled,
                            role = Role.Switch,
                            onValueChange = onCheckedChange,
                        )
                } else Modifier
            )
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showStatusLabel) {
            Text(
                text = if (checked) "开启" else "关闭",
                style = MaterialTheme.typography.bodyMedium,
                color = metro.onBackgroundMuted,
            )
            Spacer(Modifier.width(10.dp))
        }
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 24.dp)
                .border(2.dp, metro.onBackground, RectangleShape)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(trackFill)
            )
            val travelDp = 32.dp
            val thumbOffset = travelDp * thumbProgress
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(metro.onBackground)
            )
        }
    }
}

@Composable
fun MetroSolidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = LocalMetroUiState.current.accent,
) {
    val metro = LocalMetroUiState.current
    val fg = if (containerColor.luminance() > 0.55f) Color.Black else Color.White
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .alpha(if (enabled) 1f else 0.42f)
            .background(containerColor, RectangleShape)
            .then(if (enabled) Modifier.metroTilt(5f, 0.96f).clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MetroOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = LocalMetroUiState.current.onBackground,
    textColor: Color = borderColor,
) {
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .alpha(if (enabled) 1f else 0.42f)
            .border(2.dp, borderColor, RectangleShape)
            .then(if (enabled) Modifier.metroTilt(5f, 0.96f).clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun MetroSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    val metro = LocalMetroUiState.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = metro.accent,
        modifier = modifier.padding(top = 12.dp, bottom = 6.dp),
    )
}

@Composable
fun MetroDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
) {
    val metro = LocalMetroUiState.current
    HorizontalDivider(modifier = modifier, thickness = thickness, color = metro.divider)
}

/**
 * Windows Phone subpage header:
 * ← PARENT_UPPERCASE (13sp SemiBold accent)
 * Page Title (30sp Light onBackground)
 */
@Composable
fun MetroSubPageHeader(
    parentTitle: String,
    pageTitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val metro = LocalMetroUiState.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(metro.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .metroTilt(4f, 0.98f)
                    .clickable(onClick = onBack)
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = metro.accent,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = parentTitle.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = metro.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
        Text(
            text = pageTitle,
            style = MaterialTheme.typography.headlineLarge,
            color = metro.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/**
 * Windows Phone Pivot scaffold:
 * Uppercase app header + horizontal swipeable giant ExtraLight/Light Pivot tabs +
 * HorizontalPager body (with W10M accent underline when era == LATE_W10M).
 */
@Composable
fun MetroPivotScaffold(
    appTitle: String,
    tabTitles: List<String>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    headerActions: @Composable RowScope.() -> Unit = {},
    topExtraContent: @Composable ColumnScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    pageContent: @Composable (Int) -> Unit,
) {
    val metro = LocalMetroUiState.current
    val scope = rememberCoroutineScope()
    val tabScroll = rememberScrollState()
    val density = LocalDensity.current
    val tabBounds = remember(tabTitles.size) { MutableList(tabTitles.size) { 0 to 0 } }

    LaunchedEffect(pagerState.currentPage, tabTitles.size) {
        val pos = tabBounds.getOrNull(pagerState.currentPage)
        if (pos != null && pos.second > 0) {
            val targetScroll = (pos.first - with(density) { 20.dp.roundToPx() }).coerceAtLeast(0)
            tabScroll.animateScrollTo(targetScroll)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(metro.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 10.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = appTitle,
                style = MaterialTheme.typography.labelMedium,
                color = metro.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically, content = headerActions)
        }

        Column(modifier = Modifier.fillMaxWidth(), content = topExtraContent)

        val tabSp = when (metro.era) {
            MetroEra.EARLY_WP7 -> 42f
            MetroEra.MID_WP81 -> 36f
            MetroEra.LATE_W10M -> 26f
        }
        val tabWeight = if (metro.era == MetroEra.LATE_W10M) FontWeight.Light else FontWeight.ExtraLight
        val tabLetterSpacing = if (metro.era == MetroEra.LATE_W10M) 0.em else (-0.015).em

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScroll)
                .padding(start = 20.dp, end = 220.dp, bottom = 6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            tabTitles.forEachIndexed { index, title ->
                val active = index == pagerState.currentPage
                Column(
                    modifier = Modifier
                        .padding(end = 26.dp)
                        .onGloballyPositioned { coords ->
                            tabBounds[index] = coords.positionInParent().x.roundToInt() to coords.size.width
                        }
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                        .padding(bottom = 4.dp),
                ) {
                    Text(
                        text = title,
                        fontSize = (tabSp * metro.fontScale).sp,
                        lineHeight = ((tabSp + 4f) * metro.fontScale).sp,
                        fontWeight = tabWeight,
                        letterSpacing = tabLetterSpacing,
                        color = if (active) metro.onBackground else metro.onBackgroundSubtle,
                        maxLines = 1,
                    )
                    if (metro.era == MetroEra.LATE_W10M) {
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .background(if (active) metro.accent else Color.Transparent)
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            pageContent(page)
        }

        bottomBar()
    }
}

@Composable
fun MetroSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = { onQueryChange("") },
) {
    val metro = LocalMetroUiState.current
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .background(metro.surface, RectangleShape)
            .border(
                width = 2.dp,
                color = if (focused || query.isNotEmpty()) metro.accent else metro.divider,
                shape = RectangleShape,
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = if (focused || query.isNotEmpty()) metro.accent else metro.onBackgroundMuted,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = metro.onBackgroundSubtle,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = metro.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                ),
                cursorBrush = SolidColor(metro.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "清除",
                tint = metro.onBackground,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onClear),
            )
        }
    }
}

@Composable
fun MetroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    onDragStateChange: ((Boolean) -> Unit)? = null,
    thumbTag: String? = null,
) {
    val metro = LocalMetroUiState.current
    val min = valueRange.start
    val max = valueRange.endInclusive.coerceAtLeast(min + 1e-5f)
    val fraction = ((value - min) / (max - min)).coerceIn(0f, 1f)
    var trackWidthPx by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current
    val thumbWidthDp = 10.dp
    val thumbWidthPx = with(density) { thumbWidthDp.toPx() }

    fun snap(rawFraction: Float): Float {
        val clamped = rawFraction.coerceIn(0f, 1f)
        val stepped = if (steps > 0) {
            val count = steps + 1
            (clamped * count).roundToInt().toFloat() / count
        } else clamped
        return min + stepped * (max - min)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .alpha(if (enabled) 1f else 0.42f)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(value.coerceIn(min, max), min..max, steps)
                if (enabled) {
                    setProgress { targetValue ->
                        val snapped = snap((targetValue - min) / (max - min))
                        if (snapped == value) {
                            false
                        } else {
                            onValueChange(snapped)
                            onValueChangeFinished?.invoke()
                            true
                        }
                    }
                }
            }
            .onSizeChanged { trackWidthPx = it.width.toFloat().coerceAtLeast(1f) }
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(min, max, steps) {
                            detectTapGestures { offset ->
                                onValueChange(snap(offset.x / trackWidthPx))
                                onValueChangeFinished?.invoke()
                            }
                        }
                        .pointerInput(min, max, steps) {
                            detectHorizontalDragGestures(
                                onDragStart = { onDragStateChange?.invoke(true) },
                                onDragEnd = {
                                    onDragStateChange?.invoke(false)
                                    onValueChangeFinished?.invoke()
                                },
                                onDragCancel = {
                                    onDragStateChange?.invoke(false)
                                    onValueChangeFinished?.invoke()
                                },
                            ) { change, _ ->
                                change.consume()
                                onValueChange(snap(change.position.x / trackWidthPx))
                            }
                        }
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(metro.divider)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .height(3.dp)
                .background(metro.accent)
        )
        val usablePx = (trackWidthPx - thumbWidthPx).coerceAtLeast(0f)
        Box(
            modifier = Modifier
                .offset { IntOffset((usablePx * fraction).roundToInt(), 0) }
                .size(width = thumbWidthDp, height = 22.dp)
                .background(metro.onBackground, RectangleShape)
                .then(if (thumbTag != null) Modifier.testTag(thumbTag) else Modifier)
        )
    }
}

@Composable
fun MetroRadioIndicator(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val metro = LocalMetroUiState.current
    Box(
        modifier = modifier
            .size(22.dp)
            .border(2.dp, if (selected) metro.accent else metro.onBackgroundMuted, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(metro.accent, CircleShape)
            )
        }
    }
}

@Composable
fun MetroCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val metro = LocalMetroUiState.current
    Box(
        modifier = modifier
            .size(22.dp)
            .alpha(if (enabled) 1f else 0.42f)
            .background(if (checked) metro.accent else Color.Transparent, RectangleShape)
            .border(2.dp, if (checked) metro.accent else metro.onBackgroundMuted, RectangleShape)
            .then(
                if (enabled && onCheckedChange != null) Modifier.clickable { onCheckedChange(!checked) }
                else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = metro.onAccent,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

@Composable
fun MetroStatusBanner(
    title: String,
    subtitle: String,
    buttonText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    accentBarColor: Color = LocalMetroUiState.current.accent,
) {
    val metro = LocalMetroUiState.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(metro.surface, RectangleShape)
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(accentBarColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = metro.onBackground,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = metro.onBackgroundMuted,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetroSolidButton(
                    text = buttonText,
                    onClick = onAction,
                    containerColor = accentBarColor,
                    modifier = Modifier.weight(1f),
                )
                if (dismissText != null && onDismiss != null) {
                    MetroOutlinedButton(
                        text = dismissText,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/**
 * Windows Phone flat sharp dialog matching `MetroUiBuilders.showDialog`.
 */
@Composable
fun MetroAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
) {
    val metro = LocalMetroUiState.current
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 420.dp)
                .border(1.dp, metro.divider, RectangleShape),
            shape = RectangleShape,
            color = metro.chrome,
            contentColor = metro.onBackground,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                if (title != null) {
                    Box(modifier = Modifier.padding(bottom = 12.dp)) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.headlineMedium.copy(
                                color = metro.onBackground
                            )
                        ) {
                            title()
                        }
                    }
                }
                if (text != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(bottom = 16.dp)
                    ) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.bodyLarge.copy(
                                color = metro.onBackgroundMuted
                            )
                        ) {
                            text()
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}

data class MetroAppBarAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

data class MetroAppBarMenuItem(
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Windows Phone bottom Application Bar with circular outlined icon buttons and "•••" menu drawer.
 */
@Composable
fun MetroAppBar(
    actions: List<MetroAppBarAction>,
    modifier: Modifier = Modifier,
    menuItems: List<MetroAppBarMenuItem> = emptyList(),
) {
    val metro = LocalMetroUiState.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(metro.chrome)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 44.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions.take(4).forEach { action ->
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .metroTilt(6f, 0.94f)
                            .border(1.6.dp, metro.onBackground, CircleShape)
                            .clickable {
                                expanded = false
                                action.onClick()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = metro.onBackground,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            if (actions.isNotEmpty() || menuItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "•••",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = metro.onBackground,
                    )
                }
            }
        }
        if (expanded && actions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            ) {
                actions.take(4).forEach { action ->
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = metro.onBackgroundMuted,
                        maxLines = 1,
                        modifier = Modifier.widthIn(min = 38.dp),
                    )
                }
            }
        }
        if (expanded && menuItems.isNotEmpty()) {
            menuItems.forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.titleLarge,
                    color = metro.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .metroTilt(4f, 0.98f)
                        .clickable {
                            expanded = false
                            item.onClick()
                        }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
        }
    }
}
