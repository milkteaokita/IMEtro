package com.kingzcheung.xime.ui.settings

import android.app.Activity
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.twotone.CreateNewFolder
import androidx.compose.material.icons.twotone.Email
import androidx.compose.material.icons.twotone.Folder
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.icons.twotone.Link
import androidx.compose.material.icons.twotone.Phone
import androidx.compose.material.icons.twotone.Place
import androidx.compose.material.icons.twotone.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import com.kingzcheung.xime.ui.i18n.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingzcheung.xime.clipboard.ClipboardFilter
import com.kingzcheung.xime.clipboard.ClipboardGroup
import com.kingzcheung.xime.clipboard.ClipboardGroupStore
import com.kingzcheung.xime.clipboard.ClipboardTab
import com.kingzcheung.xime.clipboard.clipboardGroupNameError
import com.kingzcheung.xime.ui.menubar.dragOrderItem
import com.kingzcheung.xime.ui.menubar.rememberDragOrder
import com.kingzcheung.xime.ui.theme.MaterialLevel
import com.kingzcheung.xime.ui.theme.VisualStyles
import com.kingzcheung.xime.ui.theme.visualMaterial

/** 设置页可以混排自带分类和自建分组；「全部」固定在键盘首位，「新建分组」是列表末尾的操作。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipboardGroupSettingsContent(onBack: () -> Unit) {
    val context = LocalContext.current
    val store = remember(context) { ClipboardGroupStore.getInstance(context) }
    val state by store.state.collectAsState()
    // Opened from the keyboard: null otherwise, "" from 管理分组, or the record whose 分组 menu offered
    // 新建分组 (the group made here takes it). Back returns to the app the keyboard was in, not to the
    // settings home (tablet 11-05).
    val entry: String? = rememberSaveable { mutableStateOf(store.takeKeyboardEntry()) }.value
    var pendingRecord by rememberSaveable { mutableStateOf(entry?.ifEmpty { null }) }
    val leave: () -> Unit = if (entry == null) onBack else {
        { generateSequence(context) { (it as? ContextWrapper)?.baseContext }.filterIsInstance<Activity>().firstOrNull()
            ?.finish() ?: onBack() }
    }
    BackHandler(enabled = entry != null, onBack = leave)
    // Name dialog: null closed; a group being renamed, or a new one (id "").
    var naming by remember { mutableStateOf<ClipboardGroup?>(null) }
    var deleting by remember { mutableStateOf<ClipboardGroup?>(null) }
    val tabs = remember(state.groups, state.tabOrder) { state.sortableTabs().associateBy { it.key } }
    val dragOrder = rememberDragOrder(tabs.keys.toList(), store::setTabOrder)
    val rows = dragOrder.order.mapNotNull(tabs::get)
    Scaffold(
        containerColor = com.kingzcheung.xime.ui.metro.LocalMetroUiState.current.background,
        topBar = {
            com.kingzcheung.xime.ui.metro.MetroSubPageHeader(
                parentTitle = "剪贴板",
                pageTitle = "剪贴板分组",
                onBack = leave,
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).clipToBounds()) {
            ClipboardGroupBackground(dragOrder.list)
            LazyColumn(
                modifier = Modifier.fillMaxSize().settingsGutterScroll(dragOrder.list),
                state = dragOrder.list,
                contentPadding = keyboardPreviewContentPadding(bottom = 24.dp),
            ) {
                item(key = "heading") {
                    Text("分组", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.settingsSearchAnchor("分组").padding(bottom = 8.dp))
                }
                itemsIndexed(rows, key = { _, tab -> tab.key }) { index, tab ->
                    ClipboardSettingsRow(first = index == 0, last = false, lifted = dragOrder.dragging == tab.key,
                        modifier = dragOrderItem(dragOrder, tab.key).testTag("clipboard-order:${tab.key}")) {
                        when (tab) {
                            is ClipboardTab.Category -> SettingsToggleItem(
                                icon = tab.filter.icon(), title = tab.label, subtitle = tab.filter.description(),
                                checked = tab.filter !in state.hidden,
                                onCheckedChange = { if (dragOrder.dragging == null) store.setHidden(tab.filter, !it) },
                            )
                            is ClipboardTab.Group -> ClipboardGroupRow(tab.label, "${state.count(tab.group.id)} 条",
                                Modifier.testTag("clipboard-group-row:${tab.group.id}"),
                                onRename = { if (dragOrder.dragging == null) naming = tab.group },
                                onDelete = { if (dragOrder.dragging == null) deleting = tab.group })
                        }
                    }
                }
                item(key = "add") {
                    ClipboardSettingsRow(first = false, last = true) {
                        SettingsItem(icon = Icons.TwoTone.CreateNewFolder, title = "新建分组",
                            subtitle = "比如常用语、工作、家人", onClick = { naming = ClipboardGroup("", "") })
                    }
                }
                item(key = "note") {
                    SettingsNote("长按拖动调整顺序。关掉的分类不显示，记录仍在「全部」里。")
                }
            }
        }
    }
    naming?.let { group ->
        ClipboardGroupNameDialog(group, state.groups, onDismiss = { naming = null }) { name ->
            val error = if (group.id.isEmpty()) store.add(name, listOfNotNull(pendingRecord)) else store.rename(group.id, name)
            if (error == null) {
                naming = null
                if (group.id.isEmpty()) pendingRecord = null
            }
            error
        }
    }
    deleting?.let { group ->
        val count = state.count(group.id)
        com.kingzcheung.xime.ui.metro.MetroAlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("删除分组「${group.name}」？") },
            text = { Text(if (count == 0) "分组里没有记录。" else "里面的 $count 条记录不会删除，仍在「全部」里。") },
            confirmButton = {
                com.kingzcheung.xime.ui.metro.MetroSolidButton("删除", onClick = { store.delete(group.id); deleting = null },
                    modifier = Modifier.testTag("clipboard-group-delete-confirm"))
            },
            dismissButton = { com.kingzcheung.xime.ui.metro.MetroOutlinedButton("取消", onClick = { deleting = null }) },
        )
    }
}

@Composable
private fun ClipboardGroupBackground(list: LazyListState) {
    val metro = com.kingzcheung.xime.ui.metro.LocalMetroUiState.current
    Spacer(Modifier.layout { measurable, constraints ->
        val info = list.layoutInfo
        val visible = info.visibleItemsInfo
        val top = visible.firstOrNull { it.key == "heading" }?.let { it.offset + it.size } ?: 0
        val bottom = visible.firstOrNull { it.key == "add" }?.let { it.offset + it.size }
            ?: visible.firstOrNull { it.key == "note" }?.offset ?: constraints.maxHeight
        val card = measurable.measure(Constraints.fixed(constraints.maxWidth, (bottom - top).coerceAtLeast(0)))
        layout(constraints.maxWidth, constraints.maxHeight) { if (visible.isNotEmpty()) card.place(0, top) }
    }.background(metro.background))
}

@Composable
private fun ClipboardSettingsRow(first: Boolean, last: Boolean, modifier: Modifier = Modifier,
    lifted: Boolean = false, content: @Composable () -> Unit) {
    Box(modifier.fillMaxWidth()
        .then(if (lifted) Modifier.background(com.kingzcheung.xime.ui.metro.LocalMetroUiState.current.chrome) else Modifier)) { content() }
}

@Composable
private fun ClipboardGroupRow(name: String, detail: String, modifier: Modifier = Modifier,
                              onRename: () -> Unit, onDelete: () -> Unit) {
    val metro = com.kingzcheung.xime.ui.metro.LocalMetroUiState.current
    Row(modifier.fillMaxWidth().clickable(onClick = onRename).padding(start = 4.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(metro.accent), contentAlignment = Alignment.Center) {
            Icon(Icons.TwoTone.Folder, contentDescription = null, tint = metro.onAccent,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleLarge, color = metro.onBackground)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = metro.onBackgroundMuted)
        }
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = onRename) {
            Icon(Icons.Default.Edit, contentDescription = "改名", tint = metro.onBackgroundMuted)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "删除", tint = metro.onBackgroundMuted)
        }
    }
}

/** [save] returns null when saved, otherwise why the name cannot be used (shown under the field). */
@Composable
private fun ClipboardGroupNameDialog(group: ClipboardGroup, groups: List<ClipboardGroup>, onDismiss: () -> Unit,
                                     save: (String) -> String?) {
    var name by remember(group.id) { mutableStateOf(group.name) }
    var error by remember(group.id) { mutableStateOf<String?>(null) }
    // A name that only repeats the current one, or is still empty, is not an error worth showing yet.
    val problem = if (name.isBlank() || name.trim() == group.name) null else clipboardGroupNameError(name, groups, group.id.ifEmpty { null })
    com.kingzcheung.xime.ui.metro.MetroAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (group.id.isEmpty()) "新建分组" else "改名") },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it; error = null },
                label = { Text("分组名称") }, singleLine = true, isError = (error ?: problem) != null,
                supportingText = (error ?: problem)?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("clipboard-group-name"))
        },
        confirmButton = {
            com.kingzcheung.xime.ui.metro.MetroSolidButton("确定", onClick = { error = save(name) }, enabled = name.isNotBlank() && problem == null,
                modifier = Modifier.testTag("clipboard-group-name-save"))
        },
        dismissButton = { com.kingzcheung.xime.ui.metro.MetroOutlinedButton("取消", onClick = onDismiss) },
    )
}

private fun ClipboardFilter.icon(): ImageVector = when (this) {
    ClipboardFilter.LINK -> Icons.TwoTone.Link
    ClipboardFilter.ADDRESS -> Icons.TwoTone.Place
    ClipboardFilter.PHONE -> Icons.TwoTone.Phone
    ClipboardFilter.EMAIL -> Icons.TwoTone.Email
    ClipboardFilter.IMAGE -> Icons.TwoTone.Image
    else -> Icons.TwoTone.TextFields
}

private fun ClipboardFilter.description(): String = when (this) {
    ClipboardFilter.TEXT -> "所有文字记录"
    ClipboardFilter.LINK -> "含网址的记录"
    ClipboardFilter.ADDRESS -> "含地址的记录"
    ClipboardFilter.PHONE -> "含电话号码的记录"
    ClipboardFilter.EMAIL -> "含邮箱的记录"
    ClipboardFilter.IMAGE -> "复制的图片和最近截图"
    else -> ""
}
