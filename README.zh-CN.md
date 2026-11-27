<h1 align="center">IMEtro</h1>

<p align="center">
  <strong>Windows Phone 7 / 8.1 / 10 Mobile Metro 风格离线 Android 输入法</strong><br/>
  <a href="README.md">繁體中文</a> · <a href="README.en.md">English</a>
</p>

**IMEtro**（v2.0.0）是基于开源输入法 [CyIME](https://github.com/Cyletix/CyIME)（fork 自 [Xime](https://github.com/ximeiorg/Xime)）打造的 **Windows Phone Metro** 风格 Android 输入法，底层采用 [Rime](https://rime.im/) 引擎。从设置页面、向导、键盘面板、动态磁贴菜单到实时字幕，全部重绘为纯粹的 **0dp 锐利直角、OLED 纯黑高对比与 Windows Phone 动态磁贴美学**，并完整保留离线词库、手写、语音识别、拍照识字与实时字幕等全部能力。

---

## Metro 设计亮点

- **100% 纯正 Metro 视觉语言**：全界面采用 `0.dp` 锐利几何方块（`RectangleShape`）、零阴影扁平层、纯黑（`#000000`）／纯白（`#FFFFFF`）OLED 高对比底色，彻底告别圆角卡片与原生 Material 控件。
- **Metro 时代风格切换**：支持 **Early WP7**、**Mid WP8.1** 与 **Late W10M** 三种时代排版尺度与选中指示（Pivot 大标题＋W10M 强调色下划线）。
- **14 种经典 Metro 强调色与桌面联动**：内置 Zune 桃红、Cobalt 钴蓝、Metro Cyan 等 14 款强调色，并支持通过 `MetroThemeSync` 与 Metro Launcher（`com.metro.launcher`）实时同步深浅色与强调色。
- **标志性 Metro 动效体验**：设置首页采用 5 标签 **Metro Pivot** 布局，配合 **3D Turnstile** 页面转场与 **3D Tilt** 按压倾斜反馈（字母／数字按键保持零延迟无倾斜）；键盘菜单采用 **Metro Live Tile** 动态磁贴网格。
- **全功能本地离线运行**：词库、手写、语音识别、拍照 OCR 与实时字幕全部在手机本地运行，断网也能完整使用。
- **多语言与多方案**：全拼 26 键、九键、14 键；六种内置双拼并支持自建方案；笔画、仓颉五代、速成、注音大千、五笔 86／98；英语、日语、韩语、西语、俄语、法语、德语、意大利语、越南语、马来语。
- **手写与语音转文本**：支持中／英／日文连写、LaTeX 数学公式识别与 S Pen；本地离线语音识别（SenseVoice、Paraformer、Zipformer 等），长按空格即可说话。
- **无障碍实时字幕**：实时将周围语音转为对话字幕或横向时间线，支持说话人区分、通话字幕、后台浮窗与叫名字震动提醒。

---

## 下载与安装

需要 Android 9.0（API 28）及以上版本。

1. 前往 [**milkteaokita/IMEtro Releases**](https://github.com/milkteaokita/IMEtro/releases) 下载最新安装包：
   - **标准 / 轻量版** `IMEtro-<版本>-arm64-v8a.apk`（或 `-universal.apk`）：可在「扩展 › 模型」按需下载语音、手写和联想模型。
   - **完整版** `IMEtro-<版本>-full-arm64-v8a.apk`：内置 SenseVoice 语音、手写、声纹与联想模型，装上即离线可用。
2. 安装后根据 **IMEtro 设置向导** 在系统设置中启用 **IMEtro**，并设为当前输入法。
3. 后续可在「设置 › 关于 IMEtro › 检查更新」获取 GitHub Releases 最新版本；问题与建议欢迎提交至 [Issues](https://github.com/milkteaokita/IMEtro/issues)。

> 应用安装 ID 为 `com.metro.cyime`，与上游 `com.cyletix.cyime` / `com.kingzcheung.xime` 彼此独立，可共存而不覆盖数据。

---

## 主要功能

以下标有「默认关」的功能可在 **IMEtro 设置** 中开启。

- **中文输入**：全拼 26/9/14 键（雾凇拼音）、6 种内置与自建双拼、笔画／仓颉／速成／注音／五笔 86/98、繁体输出、模糊音、生僻字、中英混输、中文滑行、拼音与中英释义注释、词库导入与查词。
- **多语言**：英文整词补全与滑行；日语罗马字 26 键与假名键盘；西、德、法、意、俄、韩、越、马多语言键盘（默认关）。
- **多模态输入**：中英日连写与公式手写（LaTeX）、离线语音识别（SenseVoice / Paraformer / Zipformer / X-ASR / 日语）或自定义在线接口、剪贴板拍照 OCR 识字。
- **实时字幕**：支持中／英／日三语实时字幕、说话人标记、通话字幕、后台浮窗与 3 天本地字幕检索。
- **键盘与剪贴板**：固定／悬浮（拖动、缩放、旋转）／分体三种模式；QWERTY、Colemak、QWJRTK、Cyletix10、六边形蜂窝及自定义布局；动态磁贴菜单排序；剪贴板历史、固定、分组、导出（TXT / Markdown / JSON）、验证码提取、Windows 同步及 Lua 插件扩展（`.xipk`）。

---

## 构建项目

```bash
# 克隆项目（含子模块）
git clone --recursive https://github.com/milkteaokita/IMEtro.git
cd IMEtro

# 若已克隆，初始化子模块
git submodule update --init --recursive

# 调试包 / 发布包 / 离线无联网权限包
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew assembleOffline
```

- 技术栈：Kotlin · Jetpack Compose · Metro Design System · librime (JNI / C++) · ONNX Runtime · sherpa-onnx · OpenCV。
- 源码内部命名空间沿用 `com.kingzcheung.xime`，应用包名为 `com.metro.cyime`。

---

## 致谢

**IMEtro** 基于以下优秀开源项目、词库与模型构建，感谢所有作者与贡献者（完整清单请见 [README.md](README.md) 及应用内「设置 › 关于 IMEtro › 鸣谢／开源许可证」）：

| 项目 | 作者 | 说明 | 许可证 |
| --- | --- | --- | --- |
| [**CyIME**](https://github.com/Cyletix/CyIME) | Cyletix | IMEtro 上游项目（完整输入引擎与多模态基础） | GPL-3.0 |
| [**Xime**](https://github.com/ximeiorg/Xime) | Kingz Cheung | CyIME 上游原始项目 | GPL-3.0 |
| [Rime (librime)](https://github.com/rime/librime) | 佛振及 Rime 社区 | 核心输入法引擎 | BSD-3-Clause |
| [雾凇拼音 rime-ice](https://github.com/iDvel/rime-ice) | iDvel | 拼音词库与基础方案 | GPL-3.0 |
| [sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) | k2-fsa | 本地离线语音识别运行时 | Apache-2.0 |
| [OpenCC](https://github.com/BYVoid/OpenCC) | BYVoid | 简繁转换 | Apache-2.0 |

---

## 许可证

遵循 **[GNU GPLv3](LICENSE)** 开源协议（衍生自 [CyIME](https://github.com/Cyletix/CyIME) 与 [Xime](https://github.com/ximeiorg/Xime)），保留原有版权声明；第三方依赖与模型遵循各自许可证（部分模型仅限非商业用途）。
