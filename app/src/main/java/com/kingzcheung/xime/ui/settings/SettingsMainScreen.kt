package com.kingzcheung.xime.ui.settings

import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.twotone.AppSettingsAlt
import androidx.compose.material.icons.twotone.AutoAwesome
import androidx.compose.material.icons.twotone.ContentPaste
import androidx.compose.material.icons.twotone.Draw
import androidx.compose.material.icons.twotone.GraphicEq
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Keyboard
import androidx.compose.material.icons.twotone.KeyboardAlt
import androidx.compose.material.icons.twotone.Language
import androidx.compose.material.icons.twotone.MoreHoriz
import androidx.compose.material.icons.twotone.Palette
import androidx.compose.material.icons.twotone.QueryStats
import androidx.compose.material.icons.twotone.Storefront
import androidx.compose.material.icons.twotone.Swipe
import androidx.compose.material.icons.twotone.TableChart
import androidx.compose.material.icons.twotone.ToggleOn
import androidx.compose.material.icons.twotone.Tune
import androidx.compose.material.icons.twotone.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.kingzcheung.xime.R
import com.kingzcheung.xime.settings.SettingsPreferences
import com.kingzcheung.xime.ui.i18n.Text
import com.kingzcheung.xime.ui.metro.LocalMetroUiState
import com.kingzcheung.xime.ui.metro.MetroDivider
import com.kingzcheung.xime.ui.metro.MetroPivotScaffold
import com.kingzcheung.xime.ui.metro.MetroStatusBanner
import com.kingzcheung.xime.ui.metro.metroTilt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SettingsMainContent(
    onNavigateToSchema: () -> Unit,
    onNavigateToMarket: () -> Unit = {},
    onNavigateToTheme: () -> Unit,
    onNavigateToKeyEffect: () -> Unit,
    onNavigateToLayoutDisplay: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToPlugins: () -> Unit,
    onNavigateToModelLocal: () -> Unit = {},
    onNavigateToHandwritingTraining: () -> Unit = {},
    onNavigateToSmartPrediction: () -> Unit,
    onNavigateToSpeechToText: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToClipboard: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {},
    onNavigateToHardwareKeyboard: () -> Unit = {},
    onNavigateToLanguages: () -> Unit = {},
    onNavigateToRoute: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val metro = LocalMetroUiState.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var entryStatus by remember { mutableStateOf(readMainEntryStatus(context)) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        // 系统输入法选择框关闭、插拔键盘都不一定触发 resume，停留在本页时定时刷新。
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            while (true) { entryStatus = readMainEntryStatus(context); kotlinx.coroutines.delay(500) }
        }
    }
    val waitingModelBytes by com.kingzcheung.xime.model.DefaultModelInstaller.waitingForWifiBytes.collectAsState()
    val updateState by com.kingzcheung.xime.update.AppUpdater.state.collectAsState()
    LaunchedEffect(Unit) { com.kingzcheung.xime.update.AppUpdater.checkIfDue(context) }
    var showReleaseNotesNotice by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showReleaseNotesNotice = withContext(Dispatchers.IO) { com.kingzcheung.xime.update.ReleaseNotes.shouldPromptNow(context) }
    }
    val density = LocalDensity.current
    val imeBottomDp = with(density) { WindowInsets.ime.getBottom(density).toDp() }

    val tabTitles = listOf(
        stringResource(R.string.section_setup),
        stringResource(R.string.section_input),
        stringResource(R.string.section_look),
        stringResource(R.string.section_smart),
        stringResource(R.string.section_system),
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabTitles.size })

    MetroPivotScaffold(
        appTitle = stringResource(R.string.settings_title),
        tabTitles = tabTitles,
        pagerState = pagerState,
        headerActions = {
            TypingStatsTitleEntry { onNavigateToRoute(SettingsRoutes.TypingStats) }
            IconButton(
                onClick = {
                    if (searchOpen) {
                        searchOpen = false
                        searchQuery = ""
                    } else {
                        searchOpen = true
                    }
                },
            ) {
                Icon(
                    imageVector = if (searchOpen) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (searchOpen) "关闭搜索" else "搜索设置",
                    tint = if (searchOpen) metro.accent else metro.onBackground,
                )
            }
        },
        topExtraContent = {
            if (searchOpen || searchQuery.isNotBlank()) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    SettingsSearchField(searchQuery, { searchQuery = it })
                }
            }
        },
    ) { page ->
        val listState = rememberLazyListState()
        if (searchQuery.isNotBlank()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .settingsGutterScroll(listState)
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = maxOf(24.dp, imeBottomDp)),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    SettingsSearchResults(searchQuery) { entry ->
                        if (entry.target == SEARCH_TARGET_CUSTOM_LAYOUT) {
                            context.startActivity(Intent(context, com.kingzcheung.xime.CustomLayoutActivity::class.java))
                        } else onNavigateToRoute(entry.target)
                    }
                }
            }
            return@MetroPivotScaffold
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .settingsGutterScroll(listState)
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = maxOf(28.dp, imeBottomDp)),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (page) {
                0 -> {
                    if (waitingModelBytes > 0) item {
                        MetroStatusBanner(
                            title = stringResource(R.string.item_wait_wifi_title),
                            subtitle = stringResource(R.string.item_wait_wifi_summary, formatModelMegabytes(waitingModelBytes)),
                            buttonText = stringResource(R.string.status_wait_wifi),
                            onAction = { com.kingzcheung.xime.model.DefaultModelInstaller.allowMeteredDownload(context) },
                        )
                    }
                    pendingUpdate(updateState)?.let { release ->
                        item {
                            Box {
                                AppUpdateHomeNotice(release, onNavigateToAbout)
                            }
                        }
                    }
                    if (showReleaseNotesNotice) item {
                        Box {
                            ReleaseNotesHomeNotice(
                                onOpen = {
                                    showReleaseNotesNotice = false
                                    onNavigateToRoute(SettingsRoutes.ReleaseNotes)
                                },
                                onDismiss = {
                                    showReleaseNotesNotice = false
                                    com.kingzcheung.xime.update.ReleaseNotes.markSeen(context)
                                },
                            )
                        }
                    }
                    item {
                        Column {
                            MetroSetupStatusRow(
                                title = stringResource(R.string.item_enable_ime_title),
                                subtitle = stringResource(R.string.item_enable_ime_summary),
                                ready = entryStatus.imeEnabled,
                                statusText = if (entryStatus.imeEnabled) stringResource(R.string.status_set) else stringResource(R.string.status_not_set),
                                onClick = { context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                            )
                            Spacer(Modifier.height(8.dp))
                            MetroSetupStatusRow(
                                title = stringResource(R.string.item_select_ime_title),
                                subtitle = stringResource(R.string.item_select_ime_summary),
                                ready = entryStatus.imeSelected,
                                statusText = if (entryStatus.imeSelected) stringResource(R.string.status_set) else stringResource(R.string.status_not_set),
                                onClick = {
                                    val imm = context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.showInputMethodPicker()
                                },
                            )
                            Spacer(Modifier.height(8.dp))
                            SettingsItem(
                                icon = Icons.TwoTone.Tune,
                                title = stringResource(R.string.item_setup_guide_title), searchKey = "按设备推荐配置",
                                subtitle = stringResource(R.string.item_setup_guide_summary),
                                onClick = { onNavigateToRoute(SettingsRoutes.SetupGuide) },
                                showArrow = true,
                            )
                        }
                    }
                }
                1 -> {
                    item {
                        Column {
                            SettingsItem(
                                icon = Icons.TwoTone.Language,
                                title = stringResource(R.string.item_languages_title), searchKey = "语言与方案",
                                subtitle = stringResource(R.string.item_languages_summary),
                                onClick = onNavigateToLanguages,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Keyboard,
                                title = stringResource(R.string.item_layouts_title), searchKey = "键盘布局",
                                subtitle = stringResource(R.string.item_layouts_summary),
                                onClick = onNavigateToLayoutDisplay,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Swipe,
                                title = stringResource(R.string.item_gestures_title), searchKey = "按键与手势",
                                subtitle = stringResource(R.string.item_gestures_summary),
                                onClick = { onNavigateToRoute("${SettingsRoutes.LayoutDisplay}?page=gestures") },
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.KeyboardAlt,
                                title = stringResource(R.string.item_hardware_title), searchKey = "物理键盘",
                                subtitle = stringResource(R.string.item_hardware_summary),
                                onClick = onNavigateToHardwareKeyboard,
                                status = if (entryStatus.keyboardConnected) stringResource(R.string.status_connected) else stringResource(R.string.status_not_connected),
                                statusTone = if (entryStatus.keyboardConnected) SettingsStatusTone.OK else SettingsStatusTone.NEUTRAL,
                            )
                        }
                    }
                }
                2 -> {
                    item {
                        Column {
                            SettingsItem(
                                icon = Icons.TwoTone.Palette,
                                title = stringResource(R.string.item_theme_title), searchKey = "主题与外观",
                                subtitle = stringResource(R.string.item_theme_summary),
                                status = "${metro.era.title.substringBefore(" •")} · ${metro.accentName}",
                                onClick = onNavigateToTheme,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.TableChart,
                                title = stringResource(R.string.item_candidates_title), searchKey = "候选词",
                                subtitle = stringResource(R.string.item_candidates_summary),
                                onClick = { onNavigateToRoute("${SettingsRoutes.LayoutDisplay}?page=candidates") },
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Vibration,
                                title = stringResource(R.string.item_key_effect_title), searchKey = "按键效果",
                                subtitle = stringResource(R.string.item_key_effect_summary),
                                onClick = onNavigateToKeyEffect,
                                showArrow = true,
                            )
                        }
                    }
                }
                3 -> {
                    item {
                        Column {
                            SettingsItem(
                                icon = Icons.AutoMirrored.TwoTone.MenuBook,
                                title = stringResource(R.string.item_dictionary_title), searchKey = "词库",
                                subtitle = stringResource(R.string.item_dictionary_summary),
                                onClick = onNavigateToDictionary,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.AutoAwesome,
                                title = stringResource(R.string.item_prediction_title), searchKey = "智能联想",
                                subtitle = stringResource(R.string.item_prediction_summary),
                                onClick = onNavigateToSmartPrediction,
                                showArrow = true,
                            )
                            MetroDivider()
                            var sttEnabled by remember(entryStatus.sttEnabled) { mutableStateOf(entryStatus.sttEnabled) }
                            SettingsToggleItem(
                                icon = Icons.TwoTone.GraphicEq,
                                title = stringResource(R.string.item_speech_title), searchKey = "语音转文本",
                                subtitle = stringResource(R.string.item_speech_summary),
                                checked = sttEnabled,
                                showArrow = true,
                                onClick = onNavigateToSpeechToText,
                                onCheckedChange = { enabled ->
                                    sttEnabled = enabled
                                    SettingsPreferences.setSttEnabled(context, enabled)
                                }
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Draw,
                                title = stringResource(R.string.item_handwriting_title), searchKey = "手写",
                                subtitle = stringResource(R.string.item_handwriting_summary),
                                onClick = { onNavigateToRoute(SettingsRoutes.Handwriting) },
                                showArrow = true,
                            )
                            MetroDivider()
                            TypingStatsSmartRow { onNavigateToRoute(SettingsRoutes.TypingStats) }
                        }
                    }
                }
                else -> {
                    item {
                        Column {
                            SettingsItem(
                                icon = Icons.TwoTone.ContentPaste,
                                title = stringResource(R.string.item_clipboard_title), searchKey = "剪贴板",
                                subtitle = stringResource(R.string.item_clipboard_summary),
                                onClick = onNavigateToClipboard,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Storefront,
                                title = stringResource(R.string.item_extensions_title), searchKey = "扩展",
                                subtitle = stringResource(R.string.item_extensions_summary),
                                onClick = onNavigateToMarket,
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.MoreHoriz,
                                title = stringResource(R.string.item_other_title), searchKey = "其他设置",
                                subtitle = stringResource(if (com.kingzcheung.xime.BuildConfig.DEV_TOOLS || com.kingzcheung.xime.BuildConfig.DEBUG)
                                    R.string.item_other_summary_with_language else R.string.item_other_summary),
                                onClick = { onNavigateToRoute(SettingsRoutes.Other) },
                                showArrow = true,
                            )
                            MetroDivider()
                            SettingsItem(
                                icon = Icons.TwoTone.Info,
                                title = stringResource(R.string.item_about_title), searchKey = "关于 CyIME",
                                subtitle = stringResource(R.string.item_about_summary),
                                onClick = onNavigateToAbout,
                                showArrow = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetroSetupStatusRow(
    title: String,
    subtitle: String,
    ready: Boolean,
    statusText: String,
    onClick: () -> Unit,
) {
    val metro = LocalMetroUiState.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(metro.surface, RectangleShape)
            .metroTilt(4f, 0.98f)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(38.dp)
                .background(if (ready) metro.accent else metro.divider)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (ready) "✓ $title" else title,
                style = MaterialTheme.typography.titleLarge,
                color = if (ready) metro.accent else metro.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = metro.onBackgroundMuted,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = statusText,
            style = MaterialTheme.typography.labelMedium,
            color = if (ready) metro.accent else metro.error,
        )
    }
}

/** 首页入口右侧的状态：输入法启用/选中、实体键盘连接、当前主题。 */
internal data class MainEntryStatus(
    val imeEnabled: Boolean = false,
    val imeSelected: Boolean = false,
    val keyboardConnected: Boolean = false,
    val themeName: String = "",
    val sttEnabled: Boolean = false,
)

internal fun readMainEntryStatus(context: android.content.Context): MainEntryStatus {
    val input = context.getSystemService(android.content.Context.INPUT_SERVICE) as? android.hardware.input.InputManager
    val devices: List<android.view.InputDevice> =
        input?.let { manager -> manager.inputDeviceIds.map { id -> manager.getInputDevice(id) }.filterNotNull() }.orEmpty()
    val theme = com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(SettingsPreferences.getKeyboardTheme(context)).name
    return MainEntryStatus(
        imeEnabled = isCyimeEnabled(context),
        imeSelected = isCyimeSelected(context),
        keyboardConnected = com.kingzcheung.xime.service.hasPhysicalTextKeyboard(devices),
        themeName = theme,
        sttEnabled = SettingsPreferences.isSttEnabled(context),
    )
}

/** 下载提示里的大小：和模型管理里写的 MB 一样按 1024 × 1024 字节算，取整。 */
internal fun formatModelMegabytes(bytes: Long): String = "${(bytes + 524_288) / 1_048_576} MB"

private val typingStatsSwitched = androidx.compose.runtime.mutableIntStateOf(0)

@Composable
private fun TypingStatsSmartRow(onClick: () -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var enabled by remember { mutableStateOf(com.kingzcheung.xime.stats.TypingStats.isEnabled(context)) }
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            enabled = com.kingzcheung.xime.stats.TypingStats.isEnabled(context)
        }
    }
    SettingsToggleItem(
        icon = Icons.TwoTone.QueryStats,
        title = "打字统计", searchKey = "打字统计",
        subtitle = "只在本机记字数、速度和码长，不记内容",
        checked = enabled,
        showArrow = true,
        onClick = onClick,
        onCheckedChange = {
            enabled = it
            com.kingzcheung.xime.stats.TypingStats.setEnabled(context, it)
            typingStatsSwitched.intValue++
        },
        costItem = com.kingzcheung.xime.settings.PerformanceCosts.Item.TYPING_STATS,
    )
}

/** 标题栏右边一行小字：今天打了多少字，点进打字统计。关掉统计后不显示。 */
@Composable
private fun TypingStatsTitleEntry(onClick: () -> Unit) {
    val context = LocalContext.current
    val metro = LocalMetroUiState.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var summary by remember { mutableStateOf<com.kingzcheung.xime.stats.TypingStatsData?>(null) }
    LaunchedEffect(lifecycle, typingStatsSwitched.intValue) {
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
            summary = if (!com.kingzcheung.xime.stats.TypingStats.isEnabled(context)) null
                else withContext(Dispatchers.IO) { com.kingzcheung.xime.stats.TypingStats.snapshot(context) }
        }
    }
    val stats = summary ?: return
    val today = com.kingzcheung.xime.stats.TypingStats.today()
    Text(
        text = "今天 ${com.kingzcheung.xime.stats.TypingStatsText.count(stats.chars(today))} 字",
        style = MaterialTheme.typography.labelMedium,
        color = metro.accent,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        modifier = Modifier
            .metroTilt(4f, 0.96f)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    )
}
