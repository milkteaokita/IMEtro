package com.kingzcheung.xime.ui.i18n

import org.junit.Assert.assertEquals
import org.junit.Test

class TraditionalUiConverterTest {
    @Test
    fun `char lookup table lengths match`() {
        assertEquals(
            TraditionalUiConverter.SIMPLIFIED_CHARS.length,
            TraditionalUiConverter.TRADITIONAL_CHARS.length,
        )
    }

    @Test
    fun `converts common taiwan IT phrases and characters`() {
        assertEquals("設定", TraditionalUiConverter.convert("设置"))
        assertEquals("剪貼簿", TraditionalUiConverter.convert("剪贴板"))
        assertEquals("隱私權政策", TraditionalUiConverter.convert("隐私策略"))
        assertEquals("開源授權條款", TraditionalUiConverter.convert("开源许可证"))
        assertEquals("使用預設鍵盤並啟用外掛", TraditionalUiConverter.convert("使用默认键盘并启用插件"))
        assertEquals("雙拼方案 · 筆畫 · 倉頡五代", TraditionalUiConverter.convert("双拼方案 · 笔画 · 仓颉五代"))
        assertEquals("台灣繁體輸出與候選列工具列", TraditionalUiConverter.convert("台湾繁体输出与候选栏工具栏"))
        assertEquals("透過網路檢查更新與備份還原", TraditionalUiConverter.convert("通过网络检查更新与备份恢复"))
    }

    @Test
    fun `preserves native Simplified Chinese label and non-Han strings`() {
        assertEquals("简体中文", TraditionalUiConverter.convert("简体中文"))
        assertEquals("IMEtro 2.0.0", TraditionalUiConverter.convert("IMEtro 2.0.0"))
        assertEquals("", TraditionalUiConverter.convert(""))
    }
}
