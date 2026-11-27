package com.kingzcheung.xime.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.kingzcheung.xime.ui.metro.MetroAccent

internal val LocalMetroThemeActive = staticCompositionLocalOf { true }

/**
 * Authentic Windows Phone / Zune HD flat Metro keyboard color schemes.
 */
object MetroKeyboardThemes {
    const val SYNC_ID = "metro_sync"
    const val ZUNE_PINK_ID = "metro_zune_pink"
    const val WP_CYAN_ID = "metro_wp_cyan"
    const val WP_COBALT_ID = "metro_wp_cobalt"
    const val EMERALD_ID = "metro_wp_emerald"
    const val AMBER_ID = "metro_wp_amber"
    const val CRIMSON_ID = "metro_wp_crimson"

    fun isMetroTheme(themeId: String?): Boolean = themeId?.startsWith("metro_") == true

    fun onAccentColor(accent: Color): Color =
        if (accent.luminance() > 0.55f) Color.Black else Color.White

    fun createSyncScheme(synced: MetroSyncedTheme = MetroThemeSync.syncedState): KeyboardColorScheme =
        buildMetroScheme(
            id = SYNC_ID,
            name = "Metro OS 連動強調色",
            accent = synced.accentColor
        )

    fun createPresets(): List<KeyboardColorScheme> =
        listOf(createSyncScheme()) + MetroAccent.entries.map { accent ->
            buildMetroScheme(accent.id, accent.displayName, accent.color)
        }

    fun resolveAccentForTheme(themeId: String, synced: MetroSyncedTheme): Pair<Color, String> {
        if (themeId == SYNC_ID) {
            return synced.accentColor to synced.accentName
        }
        MetroAccent.fromId(themeId)?.let { return it.color to it.displayName }
        val scheme = KeyboardThemes.getRenderingScheme(themeId)
        return scheme.accentDark to scheme.name
    }

    fun buildMetroScheme(id: String, name: String, accent: Color): KeyboardColorScheme {
        return KeyboardColorScheme(
            id = id,
            name = name,
            specialKeyLight = Color(0xFFD4D4D4),
            specialKeyDark = Color(0xFF141414),
            accentLight = accent,
            accentDark = accent,
            primaryLight = accent,
            primaryDark = accent,
            primaryContainerLight = Color(0xFFD4D4D4),
            primaryContainerDark = Color(0xFF1F1F1F),
            surfaceLight = Color(0xFFFFFFFF),
            surfaceDark = Color(0xFF000000),
            keyboardBgLight = Color(0xFFFFFFFF),
            keyboardBgDark = Color(0xFF000000),
            keyBgLight = Color(0xFFE6E6E6),
            keyBgDark = Color(0xFF1F1F1F),
            candidateBarBgLight = Color(0xFFFFFFFF),
            candidateBarBgDark = Color(0xFF000000),
            keyTextColorLight = Color(0xFF000000),
            keyTextColorDark = Color(0xFFFFFFFF),
            candidateTextColorLight = Color(0xFF222222),
            candidateTextColorDark = Color(0xFFE5E5E5),
            candidateSelectedTextColorLight = accent,
            candidateSelectedTextColorDark = accent,
            dividerColorLight = Color(0xFFCCCCCC),
            dividerColorDark = Color(0xFF2A2A2A),
            useThemeColors = true,
            enterKeyLight = accent,
            enterKeyDark = accent,
            specialKeyTextColorLight = Color(0xFF000000),
            specialKeyTextColorDark = Color(0xFFFFFFFF),
        )
    }

    @androidx.compose.runtime.Composable
    internal fun resolvePressedKeyColors(
        @Suppress("UNUSED_PARAMETER") baseBackground: Color,
        baseForeground: Color,
        pressed: Boolean,
        frostedEnabled: Boolean,
    ): Pair<Color, Color>? {
        if (!LocalMetroThemeActive.current || frostedEnabled || !pressed) return null
        val accent = LocalKeyboardPalette.current?.accent ?: baseForeground
        return accent to onAccentColor(accent)
    }
}
