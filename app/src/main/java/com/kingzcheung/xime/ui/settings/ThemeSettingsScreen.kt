package com.kingzcheung.xime.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.twotone.Apps
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import com.kingzcheung.xime.ui.i18n.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kingzcheung.xime.ui.metro.LocalMetroUiState
import com.kingzcheung.xime.ui.metro.MetroAccent
import com.kingzcheung.xime.ui.metro.MetroAlertDialog
import com.kingzcheung.xime.ui.metro.MetroDivider
import com.kingzcheung.xime.ui.metro.MetroEra
import com.kingzcheung.xime.ui.metro.MetroOutlinedButton
import com.kingzcheung.xime.ui.metro.MetroSectionHeader
import com.kingzcheung.xime.ui.metro.MetroSlider
import com.kingzcheung.xime.ui.metro.MetroSolidButton
import com.kingzcheung.xime.ui.metro.metroTilt
import com.kingzcheung.xime.ui.theme.KeyboardColorScheme
import com.kingzcheung.xime.ui.theme.MetroKeyboardThemes
import com.kingzcheung.xime.ui.theme.MetroThemeSync
import com.kingzcheung.xime.viewmodel.ThemeSettingsViewModel
import androidx.compose.ui.platform.LocalContext
import com.kingzcheung.xime.settings.SettingsPreferences
import com.kingzcheung.xime.settings.FrostedGlassPreferences
import com.kingzcheung.xime.settings.FrostedGlassProfiles
import com.kingzcheung.xime.ui.keyboard.rememberKeyboardInputPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsContent(
    onBack: () -> Unit,
    onThemeChanged: () -> Unit = {}
) {
    var showIconSettings by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val subPage = if (showIconSettings) "icons" else null
    SettingsSubPages(subPage, onBack = { showIconSettings = false }) { shown ->
        ThemeSettingsPage(shown, onBack, onThemeChanged,
            openIcons = { showIconSettings = true }, closeSubPage = { showIconSettings = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSettingsPage(
    subPage: String?,
    onBack: () -> Unit,
    onThemeChanged: () -> Unit,
    openIcons: () -> Unit,
    closeSubPage: () -> Unit,
) {
    val viewModel: ThemeSettingsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val glass = FrostedGlassProfiles(
        rememberKeyboardInputPreferences(false).frostedGlass,
        rememberKeyboardInputPreferences(true).frostedGlass,
    )
    if (subPage == "icons") {
        IconSettingsContent(onBack = closeSubPage, onChanged = onThemeChanged)
        return
    }
    
    var previewTheme by remember { mutableStateOf<KeyboardColorScheme?>(null) }
    val metro = LocalMetroUiState.current
    var selectedEra by remember { mutableStateOf(SettingsPreferences.getMetroEra(context)) }
    var animScale by remember { mutableFloatStateOf(SettingsPreferences.getMetroAnimationScale(context)) }
    val synced = MetroThemeSync.syncedState
    val imageOn = uiState.colorTheme == com.kingzcheung.xime.ui.theme.CustomImageTheme.ID
    val schemeId = if (imageOn) com.kingzcheung.xime.ui.theme.CustomImageTheme.baseFor(context, uiState.colorTheme)
        else com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(uiState.colorTheme).id
    val schemes = uiState.colorThemes.filter { it.id != com.kingzcheung.xime.ui.theme.CustomImageTheme.ID }
    fun applyScheme(theme: KeyboardColorScheme) {
        if (imageOn && com.kingzcheung.xime.ui.theme.CustomImageTheme.canUnderlay(theme)) {
            com.kingzcheung.xime.ui.theme.CustomImageTheme.setBase(context, theme.id)
            com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
            viewModel.refreshThemes()
            viewModel.setColorTheme(com.kingzcheung.xime.ui.theme.CustomImageTheme.ID)
        } else viewModel.setColorTheme(theme.id)
    }
    KeyboardSettingsScaffold("主题与外观", onBack, preview = previewTheme == null) { modifier ->
        val listState = rememberSettingsSearchListState()
        LazyColumn(
            state = listState,
            modifier = modifier.settingsGutterScroll(listState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = keyboardPreviewContentPadding()
        ) {
            item {
                MetroSectionHeader("Metro 时代风格")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetroEra.entries.forEach { era ->
                        val active = (if (metro.syncedFromLauncher) metro.era else selectedEra) == era
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (active) metro.accent else metro.surface, RectangleShape)
                                .metroTilt(4f, 0.98f)
                                .clickable {
                                    selectedEra = era
                                    changeAppearance(context, onThemeChanged) {
                                        SettingsPreferences.setMetroEra(context, era)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = if (active) "✓ ${era.title}" else era.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (active) metro.onAccent else metro.onBackground,
                            )
                            Text(
                                text = era.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) metro.onAccent.copy(alpha = 0.9f) else metro.onBackgroundMuted,
                            )
                        }
                    }
                }
            }

            item {
                val syncActive = schemeId == MetroKeyboardThemes.SYNC_ID
                MetroSectionHeader("Metro OS 联动")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (syncActive) metro.accent else metro.surface, RectangleShape)
                        .metroTilt(4f, 0.98f)
                        .clickable {
                            val syncScheme = com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(MetroKeyboardThemes.SYNC_ID)
                            changeAppearance(context, onThemeChanged) { applyScheme(syncScheme) }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = if (syncActive) "✓ 跟随 Metro Home 强调色与深浅模式" else "切换到 Metro OS 联动主题",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (syncActive) metro.onAccent else metro.onBackground,
                        )
                        Text(
                            text = if (synced.fromLauncher) "已连线 Metro Launcher：${synced.accentName}" else "未检测到 Metro Launcher，将使用默认 Zune 桃红",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (syncActive) metro.onAccent.copy(alpha = 0.85f) else metro.onBackgroundMuted,
                        )
                    }
                }
            }

            item {
                MetroSectionHeader("显示模式")
                val currentTheme = uiState.colorThemes.firstOrNull { it.id == uiState.colorTheme }
                    ?: com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(uiState.colorTheme)
                Row(
                    modifier = Modifier.settingsSearchAnchor("显示模式", bleed = 6.dp, bleedVertical = 6.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardThemeCard(
                        theme = currentTheme,
                        glass = glass,
                        previewAspectRatio = 1.3f,
                        isSelected = uiState.darkMode == 2,
                        onClick = {
                            changeAppearance(context, onThemeChanged) { viewModel.setDarkMode(2) }
                        },
                        modifier = Modifier.weight(1f),
                        title = "跟随系统"
                    )
                    KeyboardThemeCard(
                        theme = currentTheme,
                        glass = glass,
                        previewAspectRatio = 1.3f,
                        title = "纯白浅色",
                        isSelected = uiState.darkMode == 0,
                        previewDark = false,
                        onClick = {
                            changeAppearance(context, onThemeChanged) { viewModel.setDarkMode(0) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    KeyboardThemeCard(
                        theme = currentTheme,
                        glass = glass,
                        previewAspectRatio = 1.3f,
                        title = "OLED 纯黑",
                        isSelected = uiState.darkMode == 1,
                        previewDark = true,
                        onClick = {
                            changeAppearance(context, onThemeChanged) { viewModel.setDarkMode(1) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                MetroSectionHeader("强调色：${metro.accentName}")
                Column(
                    modifier = Modifier.settingsSearchAnchor("配色方案", bleed = 6.dp, bleedVertical = 4.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MetroAccent.entries.chunked(7).forEach { rowAccents ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowAccents.forEach { accent ->
                                val selected = schemeId == accent.id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .then(
                                            if (selected) Modifier.border(3.dp, metro.onBackground, RectangleShape).padding(3.dp)
                                            else Modifier
                                        )
                                        .background(accent.color, RectangleShape)
                                        .metroTilt(6f, 0.92f)
                                        .clickable {
                                            val scheme = com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(accent.id)
                                            changeAppearance(context, onThemeChanged) { applyScheme(scheme) }
                                        }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        MetroOutlinedButton(
                            text = "预览键盘配色",
                            onClick = {
                                previewTheme = uiState.colorThemes.firstOrNull { it.id == schemeId }
                                    ?: com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(schemeId)
                            },
                        )
                    }
                }
            }

            item {
                MetroDivider()
                MetroSectionHeader("动效与交互")
                val effectiveAnim = if (metro.syncedFromLauncher) metro.animationScale else animScale
                val animDesc = when {
                    effectiveAnim <= 0.05f -> "关闭 (0x)"
                    effectiveAnim < 0.8f -> "俐落快速 (0.65x)"
                    effectiveAnim > 1.2f -> "慢速电影感 (1.4x)"
                    else -> "经典流畅 Turnstile (1.0x)"
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .metroTilt(4f, 0.98f)
                        .clickable {
                            val next = when {
                                animScale <= 0.05f -> 1.0f
                                animScale < 0.8f -> 0.0f
                                animScale <= 1.15f -> 1.4f
                                else -> 0.65f
                            }
                            animScale = next
                            SettingsPreferences.setMetroAnimationScale(context, next)
                        }
                        .padding(vertical = 10.dp)
                ) {
                    Text("Turnstile 与 Tilt 动画节奏", style = MaterialTheme.typography.titleLarge, color = metro.onBackground)
                    Text(animDesc, style = MaterialTheme.typography.bodyMedium, color = metro.accent)
                }
            }

            item {
                // 动态背景只对图片背景起作用：当前主题有背景图（我的图片、大漠流金等）时才放出来。
                val imageTheme = (uiState.colorThemes.firstOrNull { it.id == uiState.colorTheme }
                    ?: com.kingzcheung.xime.ui.theme.KeyboardThemes.getThemeById(uiState.colorTheme))
                    .keyboardBackground?.type == "image"
                CustomImageThemeSection(currentThemeId = uiState.colorTheme, imageTheme = imageTheme, onUseImage = { on ->
                    changeAppearance(context, onThemeChanged) {
                        if (on) {
                            schemes.firstOrNull { it.id == schemeId }
                                ?.takeIf(com.kingzcheung.xime.ui.theme.CustomImageTheme::canUnderlay)
                                ?.let { com.kingzcheung.xime.ui.theme.CustomImageTheme.setBase(context, it.id) }
                            com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
                            viewModel.refreshThemes()
                            viewModel.setColorTheme(com.kingzcheung.xime.ui.theme.CustomImageTheme.ID)
                        } else viewModel.setColorTheme(schemeId)
                    }
                }, onSaved = {
                    viewModel.refreshThemes()
                    changeAppearance(context, onThemeChanged) {
                        viewModel.setColorTheme(com.kingzcheung.xime.ui.theme.CustomImageTheme.ID)
                    }
                }, onDeleted = { baseThemeId ->
                    viewModel.refreshThemes()
                    if (uiState.colorTheme == com.kingzcheung.xime.ui.theme.CustomImageTheme.ID) {
                        changeAppearance(context, onThemeChanged) { viewModel.setColorTheme(baseThemeId) }
                    } else onThemeChanged()
                }, onImageChanged = {
                    viewModel.refreshThemes()
                    onThemeChanged()
                })
            }

            item { KeyboardAppearanceSettings() }
            item {
                SettingsSection(title = "桌面图标") {
                    SettingsItem(icon = Icons.TwoTone.Apps, title = "图标设置",
                        subtitle = if (com.kingzcheung.xime.ui.theme.IconAppearance.linked) "与视觉样式同步" else "独立选择",
                        onClick = openIcons, showArrow = true)
                }
            }
        }

        previewTheme?.let { theme ->
            ThemePreviewSheet(
                theme = theme,
                onApply = {
                    changeAppearance(context, onThemeChanged) {
                        applyScheme(theme)
                        previewTheme = null
                    }
                },
                onDismiss = { previewTheme = null },
            )
        }
    }
}

/**
 * 「我的图片」上压的遮罩，一行一条：左边浅色／深色，中间滑条，右边百分比。越高图片越淡、按键文字越清楚，0% 是原图。
 * 浅色、深色键盘底色不同，同一张图要淡的程度不同，所以各一条。滑条按 5% 一档吸附，不画刻度点（原来 18 个点挤成一排）。
 * 拖动时只改数字，松手才存并刷新键盘；默认 72%，与原来一样。
 */
@Composable
private fun CustomImageVeilSlider(dark: Boolean, onChanged: () -> Unit) {
    val context = LocalContext.current
    val title = if (dark) "深色遮罩" else "浅色遮罩"
    var veil by remember { mutableStateOf(com.kingzcheung.xime.ui.theme.CustomImageTheme.veil(context, dark)) }
    Row(Modifier.settingsSearchAnchor(title).fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(if (dark) "深色" else "浅色", style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp))
        MetroSlider(
            value = veil,
            onValueChange = { veil = kotlin.math.round(it * 20f) / 20f },
            onValueChangeFinished = {
                changeAppearance(context, onChanged) {
                    com.kingzcheung.xime.ui.theme.CustomImageTheme.setVeil(context, dark, veil)
                    com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
                }
            },
            valueRange = 0f..com.kingzcheung.xime.ui.theme.CustomImageTheme.MAX_VEIL,
            modifier = Modifier.weight(1f).semantics { contentDescription = title }
                .testTag(if (dark) "custom-image-veil-dark" else "custom-image-veil-light"),
        )
        Text("${kotlin.math.round(veil * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.End, modifier = Modifier.width(44.dp))
    }
}

/**
 * 主题与外观 › 背景图片：一张卡管背景图的全部事（用户 11-13 要求按真实用途重做，设计理由见补丁的 DESIGN.md）。
 *
 * 1. 没有图片：「用自己的图片 >」，选一张图，进调整页，确认后换成「我的图片」。
 * 2. 有图片：同一行变成总开关（原来另有一行「使用背景图片」），关掉时图片留着，键盘用配色方案的底色。
 *    下面一排小图：第一张是主图（点它调整位置或换一张），后面是轮流显示的图，每张 × 删，＋ 加图。
 *    图片开着时再给浅色、深色两条遮罩。
 * 3. 当前主题有背景图（我的图片、大漠流金等）时接着放「动态背景」：它只对图片背景起作用，开了才轮流换图。
 */
@Composable
private fun CustomImageThemeSection(currentThemeId: String, imageTheme: Boolean, onUseImage: (Boolean) -> Unit,
    onSaved: () -> Unit, onDeleted: (String) -> Unit, onImageChanged: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasImage by remember { mutableStateOf(com.kingzcheung.xime.ui.theme.CustomImageTheme.hasImage(context)) }
    // 调整页正在调的图片（刚选的新图或已在用的那张）与要沿用的主题。
    var editing by remember { mutableStateOf<Pair<String, String>?>(null) }
    // 正在调位置的轮播图片（11-15）：确认只记它的摆放，取消什么都不删（图早就在用）。
    var editingSlide by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    val motion = com.kingzcheung.xime.ui.theme.rememberBackgroundMotionSettings()
    val slides = remember(motion.slides) { com.kingzcheung.xime.ui.theme.CustomImageTheme.slides(context, motion.slides) }
    val imageOn = currentThemeId == com.kingzcheung.xime.ui.theme.CustomImageTheme.ID
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val base = com.kingzcheung.xime.ui.theme.CustomImageTheme.baseFor(context, currentThemeId)
        scope.launch {
            val imported = withContext(Dispatchers.IO) {
                runCatching { com.kingzcheung.xime.ui.theme.CustomImageTheme.importImage(context, uri) }
            }
            imported.onSuccess { editing = it to base }.onFailure {
                com.kingzcheung.xime.util.FileLogger.e("Appearance", "Custom background failed", it)
                com.kingzcheung.xime.ui.i18n.UiToast.makeText(context, "这张图片读不出来，换一张试试", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
    val slidePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(com.kingzcheung.xime.settings.BackgroundMotionPreferences.MAX_SLIDES)) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            val added = withContext(Dispatchers.IO) { com.kingzcheung.xime.ui.theme.CustomImageTheme.addSlides(context, uris) }
            if (added < uris.size) com.kingzcheung.xime.ui.i18n.UiToast.makeText(context,
                if (added == 0 && slides.size >= com.kingzcheung.xime.settings.BackgroundMotionPreferences.MAX_SLIDES)
                    "最多 ${com.kingzcheung.xime.settings.BackgroundMotionPreferences.MAX_SLIDES + 1} 张"
                else "有 ${uris.size - added} 张没有加上", android.widget.Toast.LENGTH_LONG).show()
        }
    }
    fun pickOne() = picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    SettingsSection(title = "背景图片") {
        // 主图换了（调整页确认后 editing 清空）、顶上来或删了，这里跟着重组，图片排的第一张随之换。
        val main = if (hasImage) com.kingzcheung.xime.ui.theme.CustomImageTheme.imageSource(context) else null
        if (main == null) {
            SettingsItem(icon = Icons.TwoTone.Image, title = "用自己的图片",
                subtitle = "用相册里的图片做键盘背景",
                onClick = { pickOne() }, showArrow = true)
        } else {
            SettingsToggleItem(icon = Icons.TwoTone.Image, title = "用自己的图片",
                subtitle = if (imageOn) "换配色时保留背景图片"
                    else "关着时用配色的底色",
                checked = imageOn, onCheckedChange = onUseImage)
            BackgroundImageStrip(
                main = main, slides = slides, imageOn = imageOn, motionOn = motion.enabled,
                onAdjust = {
                    editing = main to com.kingzcheung.xime.ui.theme.CustomImageTheme.baseFor(context,
                        com.kingzcheung.xime.ui.theme.CustomImageTheme.ID)
                },
                onReplace = { pickOne() },
                onRemoveMain = {
                    // 只剩这一张：先确认再整个删；后面还有图：下一张顶上来，不用确认（删的只是这一张）。
                    if (slides.isEmpty()) confirmDelete = true
                    else scope.launch {
                        val promoted = withContext(Dispatchers.IO) {
                            runCatching { com.kingzcheung.xime.ui.theme.CustomImageTheme.promoteNextSlide(context) }
                        }
                        promoted.onSuccess { moved ->
                            // 刚好别处把轮播图删光了：照只剩一张处理。
                            if (!moved) { confirmDelete = true; return@onSuccess }
                            com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
                            onImageChanged()
                        }.onFailure {
                            com.kingzcheung.xime.util.FileLogger.e("Appearance", "Custom background promote failed", it)
                            com.kingzcheung.xime.ui.i18n.UiToast.makeText(context, "没有删除成功，请重试", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onAdd = { slidePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onRemoveSlide = { src -> scope.launch(Dispatchers.IO) { com.kingzcheung.xime.ui.theme.CustomImageTheme.removeSlide(context, src) } },
                onAdjustSlide = { src -> editingSlide = src },
                onApplySlideToOthers = { src ->
                    scope.launch(Dispatchers.IO) {
                        val placement = com.kingzcheung.xime.ui.theme.CustomImageTheme.slidePlacement(context, src)
                        runCatching {
                            slides.filter { it != src }.forEach {
                                com.kingzcheung.xime.settings.BackgroundMotionPreferences.setSlidePlacement(context, it, placement)
                            }
                        }.onFailure { com.kingzcheung.xime.util.FileLogger.e("Appearance", "Slide placement save failed", it) }
                    }
                },
            )
            // 遮罩只管「我的图片」：图片关着时调它看不到任何变化，不放出来。
            if (imageOn) {
                Column(Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)) {
                    Text("遮罩", style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    Text("越高图片越淡、字越清楚",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    CustomImageVeilSlider(dark = false, onChanged = onImageChanged)
                    CustomImageVeilSlider(dark = true, onChanged = onImageChanged)
                }
            }
        }
        if (imageTheme) BackgroundMotionSettingsItems()
    }
    editing?.let { (src, base) ->
        val existing = src == com.kingzcheung.xime.ui.theme.CustomImageTheme.imageSource(context)
        CustomImageEditorSheet(
            src = src,
            baseThemeId = base,
            initial = if (existing) com.kingzcheung.xime.ui.theme.CustomImageTheme.placement(context)
                else com.kingzcheung.xime.ui.theme.ThemeImagePlacement.DEFAULT,
            onConfirm = { placement ->
                scope.launch {
                    val saved = withContext(Dispatchers.IO) {
                        runCatching { com.kingzcheung.xime.ui.theme.CustomImageTheme.commit(context, src, base, placement) }
                    }
                    saved.onSuccess {
                        com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
                        hasImage = true
                        editing = null
                        onSaved()
                    }.onFailure {
                        com.kingzcheung.xime.util.FileLogger.e("Appearance", "Custom background save failed", it)
                        com.kingzcheung.xime.ui.i18n.UiToast.makeText(context, "图片没有保存成功，请重试", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            },
            onDismiss = {
                editing = null
                scope.launch(Dispatchers.IO) { com.kingzcheung.xime.ui.theme.CustomImageTheme.discard(context, src) }
            },
        )
    }
    editingSlide?.let { src ->
        CustomImageEditorSheet(
            src = src,
            baseThemeId = com.kingzcheung.xime.ui.theme.CustomImageTheme.baseFor(context, com.kingzcheung.xime.ui.theme.CustomImageTheme.ID),
            initial = remember(src) { com.kingzcheung.xime.ui.theme.CustomImageTheme.slidePlacement(context, src) },
            onConfirm = { placement ->
                scope.launch {
                    val saved = withContext(Dispatchers.IO) {
                        runCatching { com.kingzcheung.xime.settings.BackgroundMotionPreferences.setSlidePlacement(context, src, placement) }
                    }
                    saved.onSuccess { editingSlide = null }.onFailure {
                        com.kingzcheung.xime.util.FileLogger.e("Appearance", "Slide placement save failed", it)
                        com.kingzcheung.xime.ui.i18n.UiToast.makeText(context, "没有保存成功，请重试", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            },
            onDismiss = { editingSlide = null },
        )
    }
    if (confirmDelete) {
        MetroAlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("删除图片") },
            text = { Text("这是最后一张，删掉后就没有背景图片了。") },
            confirmButton = {
                MetroSolidButton(text = "删除", onClick = {
                    confirmDelete = false
                    scope.launch {
                        val deleted = withContext(Dispatchers.IO) {
                            runCatching { com.kingzcheung.xime.ui.theme.CustomImageTheme.delete(context) }
                        }
                        deleted.onSuccess { base ->
                            com.kingzcheung.xime.ui.theme.KeyboardThemes.reload(context)
                            hasImage = false
                            onDeleted(base)
                        }.onFailure {
                            com.kingzcheung.xime.util.FileLogger.e("Appearance", "Custom background delete failed", it)
                            com.kingzcheung.xime.ui.i18n.UiToast.makeText(context, "没有删除成功，请重试", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                })
            },
            dismissButton = { MetroOutlinedButton(text = "取消", onClick = { confirmDelete = false }) },
        )
    }
}
