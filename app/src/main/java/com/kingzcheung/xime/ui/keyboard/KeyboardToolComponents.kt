package com.kingzcheung.xime.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import com.kingzcheung.xime.ui.metro.LocalMetroUiState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shared tool typography and touch targets; selected colors always come as a semantic pair. */
@Composable
internal fun ToolTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val metro = LocalMetroUiState.current
    val fill = when {
        compact && selected -> metro.accent
        compact -> metro.chrome
        selected -> metro.chrome
        else -> Color.Transparent
    }
    val foreground = when {
        compact && selected -> metro.onAccent
        selected -> metro.accent
        else -> metro.onBackgroundMuted
    }
    Box(modifier.heightIn(min = if (compact) 40.dp else 44.dp)
        .clip(RectangleShape)
        .background(if (compact) frostedKeyColor(fill, foreground,
            LocalKeyboardInputPreferences.current.frostedGlass) else fill)
        .semantics { this.selected = selected }.clickable(onClick = onClick)
        .padding(horizontal = if (compact) 10.dp else 16.dp, vertical = if (compact) 6.dp else 10.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = foreground,
                fontSize = 14.sp, lineHeight = 20.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, maxLines = 1)
            if (!compact && selected) {
                Box(Modifier.padding(top = 2.dp).width(24.dp).height(2.dp).background(metro.accent))
            }
        }
    }
}

@Composable
internal fun ToolIcon(icon: ImageVector, label: String, onClick: () -> Unit, tag: String = label) {
    IconButton(onClick, Modifier.size(44.dp).testTag(tag)) {
        Icon(icon, label, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.onSurface)
    }
}
