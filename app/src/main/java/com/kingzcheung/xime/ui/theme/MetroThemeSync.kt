package com.kingzcheung.xime.ui.theme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

data class MetroSyncedTheme(
    val accentColorInt: Int = 0xFFE6007E.toInt(),
    val accentName: String = "Zune 桃紅",
    val darkMode: Boolean = true,
    val eraName: String = "EARLY_WP7",
    val animationScale: Float = 1.0f,
    val fontScale: Float = 1.0f,
    val fromLauncher: Boolean = false
) {
    val accentColor: Color
        get() = Color(accentColorInt)
}

/**
 * Live synchronization bridge with Metro OS (`com.metro.launcher`).
 * Observes `content://com.metro.launcher.theme.provider/theme` and
 * `com.metro.action.THEME_CHANGED` so keyboard & settings follow Metro accent/dark mode live.
 */
object MetroThemeSync {
    const val AUTHORITY = "com.metro.launcher.theme.provider"
    val CONTENT_URI: Uri get() = Uri.parse("content://$AUTHORITY/theme")
    const val ACTION_THEME_CHANGED = "com.metro.action.THEME_CHANGED"

    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_ACCENT_COLOR = "accent_color"
    const val KEY_ACCENT_NAME = "accent_name"
    const val KEY_ERA = "metro_era"
    const val KEY_ANIMATION_SCALE = "animation_scale"
    const val KEY_FONT_SCALE = "font_scale"

    var syncedState by mutableStateOf(MetroSyncedTheme())
        private set

    @Volatile
    private var initialized = false
    private val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    fun init(context: Context) {
        if (initialized) {
            refresh(context)
            return
        }
        initialized = true
        val appCtx = context.applicationContext
        runCatching {
            appCtx.contentResolver.registerContentObserver(
                CONTENT_URI,
                true,
                object : ContentObserver(mainHandler) {
                    override fun onChange(selfChange: Boolean) {
                        refresh(appCtx)
                    }
                }
            )
        }
        runCatching {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (intent == null || intent.action != ACTION_THEME_CHANGED) return
                    val current = syncedState
                    val accent = intent.getIntExtra(KEY_ACCENT_COLOR, current.accentColorInt)
                    val accentName = intent.getStringExtra(KEY_ACCENT_NAME) ?: current.accentName
                    val dark = if (intent.hasExtra(KEY_DARK_MODE)) {
                        intent.getBooleanExtra(KEY_DARK_MODE, current.darkMode)
                    } else {
                        current.darkMode
                    }
                    val era = intent.getStringExtra(KEY_ERA) ?: current.eraName
                    val animScale = if (intent.hasExtra(KEY_ANIMATION_SCALE)) {
                        intent.getFloatExtra(KEY_ANIMATION_SCALE, current.animationScale)
                    } else {
                        current.animationScale
                    }
                    val fontScale = if (intent.hasExtra(KEY_FONT_SCALE)) {
                        intent.getFloatExtra(KEY_FONT_SCALE, current.fontScale)
                    } else {
                        current.fontScale
                    }
                    updateSyncedState(
                        MetroSyncedTheme(
                            accentColorInt = accent,
                            accentName = accentName,
                            darkMode = dark,
                            eraName = era,
                            animationScale = animScale,
                            fontScale = fontScale,
                            fromLauncher = true
                        )
                    )
                }
            }
            ContextCompat.registerReceiver(
                appCtx,
                receiver,
                IntentFilter(ACTION_THEME_CHANGED),
                ContextCompat.RECEIVER_EXPORTED
            )
        }
        refresh(appCtx)
    }

    fun refresh(context: Context) {
        val loaded = runCatching {
            context.applicationContext.contentResolver.query(CONTENT_URI, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val darkIdx = cursor.getColumnIndex(KEY_DARK_MODE)
                    val accentIdx = cursor.getColumnIndex(KEY_ACCENT_COLOR)
                    val nameIdx = cursor.getColumnIndex(KEY_ACCENT_NAME)
                    val eraIdx = cursor.getColumnIndex(KEY_ERA)
                    val animIdx = cursor.getColumnIndex(KEY_ANIMATION_SCALE)
                    val fontIdx = cursor.getColumnIndex(KEY_FONT_SCALE)
                    val dark = if (darkIdx >= 0) cursor.getInt(darkIdx) != 0 else true
                    val accent = if (accentIdx >= 0) cursor.getInt(accentIdx) else 0xFFE6007E.toInt()
                    val name = if (nameIdx >= 0) cursor.getString(nameIdx) ?: "Metro" else "Metro"
                    val era = if (eraIdx >= 0) cursor.getString(eraIdx) ?: "EARLY_WP7" else "EARLY_WP7"
                    val anim = if (animIdx >= 0) cursor.getFloat(animIdx) else 1.0f
                    val font = if (fontIdx >= 0) cursor.getFloat(fontIdx) else 1.0f
                    MetroSyncedTheme(
                        accentColorInt = accent,
                        accentName = name,
                        darkMode = dark,
                        eraName = era,
                        animationScale = anim,
                        fontScale = font,
                        fromLauncher = true
                    )
                } else null
            }
        }.getOrNull()
        if (loaded != null) {
            updateSyncedState(loaded)
        }
    }

    private fun updateSyncedState(next: MetroSyncedTheme) {
        if (next != syncedState) {
            syncedState = next
            KeyboardThemes.refreshMetroSyncScheme()
        }
    }
}
