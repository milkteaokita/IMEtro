package com.kingzcheung.xime.ui.metro

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

enum class MetroEra(val title: String, val description: String) {
    EARLY_WP7(
        "早期 • Zune HD / WP7",
        "純黑大留白、實心方塊、超大細體標題"
    ),
    MID_WP81(
        "中期 • Windows Phone 8.1",
        "緊湊平磚、俐落對比排版"
    ),
    LATE_W10M(
        "後期 • Windows 10 Mobile",
        "細框邊線、強調色底線樞紐分頁"
    );

    companion object {
        fun fromName(name: String?): MetroEra =
            entries.firstOrNull { it.name == name } ?: EARLY_WP7
    }
}

enum class MetroAccent(val id: String, val colorLong: Long, val displayName: String) {
    ZUNE_PINK("metro_zune_pink", 0xFFE6007E, "Zune 桃紅"),
    ZUNE_ORANGE("metro_zune_orange", 0xFFFA6800, "Zune 亮橘"),
    CYAN("metro_wp_cyan", 0xFF1BA1E2, "WP 經典藍"),
    COBALT("metro_wp_cobalt", 0xFF0050EF, "鈷藍"),
    EMERALD("metro_wp_emerald", 0xFF008A00, "翡翠綠"),
    LIME("metro_wp_lime", 0xFFA4C400, "萊姆綠"),
    TEAL("metro_wp_teal", 0xFF00ABA9, "藍綠"),
    MAGENTA("metro_wp_magenta", 0xFFD80073, "洋紅"),
    CRIMSON("metro_wp_crimson", 0xFFA20025, "深紅"),
    AMBER("metro_wp_amber", 0xFFF0A30A, "琥珀"),
    VIOLET("metro_wp_violet", 0xFFAA00FF, "紫羅蘭"),
    INDIGO("metro_wp_indigo", 0xFF6A00FF, "靛紫"),
    STEEL("metro_wp_steel", 0xFF647687, "鋼鐵灰"),
    OLIVE("metro_wp_olive", 0xFF6D8764, "橄欖綠");

    val color: Color
        get() = Color(0xFF000000L or colorLong)

    val argbInt: Int
        get() = (0xFF000000L or colorLong).toInt()

    companion object {
        fun fromColor(color: Int): MetroAccent {
            val rgb = color and 0xFFFFFF
            return entries.firstOrNull { (it.colorLong.toInt() and 0xFFFFFF) == rgb } ?: ZUNE_PINK
        }

        fun fromId(id: String?): MetroAccent? =
            entries.firstOrNull { it.id == id }
    }
}

@Immutable
data class MetroUiState(
    val darkMode: Boolean = true,
    val accent: Color = MetroAccent.ZUNE_PINK.color,
    val accentName: String = MetroAccent.ZUNE_PINK.displayName,
    val era: MetroEra = MetroEra.EARLY_WP7,
    val animationScale: Float = 1.0f,
    val fontScale: Float = 1.0f,
    val syncedFromLauncher: Boolean = false,
) {
    val background: Color
        get() = if (darkMode) Color(0xFF000000) else Color(0xFFFFFFFF)

    val surface: Color
        get() = if (darkMode) Color(0xFF141414) else Color(0xFFF2F2F2)

    val chrome: Color
        get() = if (darkMode) Color(0xFF1F1F1F) else Color(0xFFE6E6E6)

    val onBackground: Color
        get() = if (darkMode) Color(0xFFFFFFFF) else Color(0xFF000000)

    val onBackgroundMuted: Color
        get() = if (darkMode) Color(0xB3FFFFFF) else Color(0x99000000)

    val onBackgroundSubtle: Color
        get() = if (darkMode) Color(0x66FFFFFF) else Color(0x55000000)

    val divider: Color
        get() = if (darkMode) Color(0x33FFFFFF) else Color(0x26000000)

    val onAccent: Color
        get() = if (accent.luminance() > 0.55f) Color.Black else Color.White

    val error: Color
        get() = if (darkMode) Color(0xFFFF4D4D) else Color(0xFFC42B1C)

    val success: Color
        get() = accent
}

val LocalMetroUiState = staticCompositionLocalOf { MetroUiState() }

fun metroComposeTypography(fontScale: Float = 1.0f): Typography {
    val s = fontScale.coerceIn(0.8f, 1.4f)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Thin,
            fontSize = (64f * s).sp,
            lineHeight = (68f * s).sp,
            letterSpacing = (-0.02).em,
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraLight,
            fontSize = (44f * s).sp,
            lineHeight = (48f * s).sp,
            letterSpacing = (-0.015).em,
        ),
        displaySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraLight,
            fontSize = (36f * s).sp,
            lineHeight = (40f * s).sp,
            letterSpacing = (-0.01).em,
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = (30f * s).sp,
            lineHeight = (36f * s).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = (24f * s).sp,
            lineHeight = (30f * s).sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = (22f * s).sp,
            lineHeight = (28f * s).sp,
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = (22f * s).sp,
            lineHeight = (28f * s).sp,
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (16f * s).sp,
            lineHeight = (22f * s).sp,
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (15f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.04.em,
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (16f * s).sp,
            lineHeight = (22f * s).sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (14f * s).sp,
            lineHeight = (20f * s).sp,
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (13f * s).sp,
            lineHeight = (18f * s).sp,
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (15f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.04.em,
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (13f * s).sp,
            lineHeight = (16f * s).sp,
            letterSpacing = 0.08.em,
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = (12f * s).sp,
            lineHeight = (16f * s).sp,
        ),
    )
}
