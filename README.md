<h1 align="center">IMEtro</h1>

<p align="center">
  <strong>Windows Phone 7 / 8.1 / 10 Mobile Metro 風格離線 Android 輸入法</strong><br/>
  <a href="README.zh-CN.md">简体中文</a> · <a href="README.en.md">English</a>
</p>

**IMEtro**（v2.0.0）是基於開源輸入法 [CyIME](https://github.com/Cyletix/CyIME)（fork 自 [Xime](https://github.com/ximeiorg/Xime)）打造的 **Windows Phone Metro** 風格 Android 輸入法，底層採用 [Rime](https://rime.im/) 引擎。從設定介面、快速設定引導、鍵盤面板、動態磚工具選單到即時字幕，全面重塑為純正 **0dp 銳利直角、純黑 OLED 高對比與 Windows Phone 動態磚美學**，同時完整保留離線詞庫、手寫、語音辨識、拍照識字與即時字幕功能。

---

## Metro 設計亮點

- **100% 純正 Metro 視覺語言**：全介面採用 `0.dp` 銳利幾何方塊（`RectangleShape`）、零陰影平面設計、純黑（`#000000`）／純白（`#FFFFFF`）OLED 高對比襯底，徹底移除圓角卡片與原生 Material 3 控制項。
- **Metro 時代風格切換**：支援 **Early WP7**、**Mid WP8.1** 與 **Late W10M** 三種時代排版尺度與互動指標（Pivot 巨型標題＋W10M 強調色底線）。
- **14 種經典 Metro 強調色與桌面連動**：內建 Zune 桃紅、Cobalt 鈷藍、Metro Cyan 等 14 款強調色，並可透過 `MetroThemeSync` 與 Metro Launcher（`com.metro.launcher`）即時同步深淺色與強調色。
- **標誌性 Metro 動態表現**：設定頁採用 5 分頁 **Metro Pivot** 架構搭配 **3D Turnstile（旋轉門）** 轉場與 **3D Tilt** 觸控下沉動態（快速打字字母／數字鍵維持零延遲無傾斜）；功能選單採用 **Metro Live Tile** 方磚網格。
- **全功能本機離線運行**：詞庫、手寫、語音辨識、拍照 OCR 與即時字幕全部在手機本機運作，不聯網也能完整使用。
- **多語言與豐富輸入方案**：全拼 26 鍵、九鍵、14 鍵；六種內建雙拼並可自訂方案；筆畫、倉頡五代、速成、注音大千、五筆 86／98；英語、日語、韓語、西語、俄語、法語、德語、義大利語、越南語、馬來語。
- **手寫與語音轉文字**：支援中／英／日文連寫、LaTeX 數學公式辨識與 S Pen；本機離線語音辨識（SenseVoice、Paraformer、Zipformer 等），長按空白鍵即可說話。
- **無障礙即時字幕**：即時將周遭語音轉成對話字幕或橫向時間軸，支援講者區分、通話字幕、後台浮窗與呼叫名字震動提醒。

---

## 下載與安裝

需要 Android 9.0（API 28）或更新版本。

1. 前往 [**milkteaokita/IMEtro Releases**](https://github.com/milkteaokita/IMEtro/releases) 下載最新安裝檔：
   - **標準 / 輕量版** `IMEtro-<版本>-arm64-v8a.apk`（或 `-universal.apk`）：可在安裝後於「擴充 › 模型」按需下載語音、手寫與聯想模型。
   - **完整版** `IMEtro-<版本>-full-arm64-v8a.apk`：內建 SenseVoice 語音、手寫、聲紋與聯想模型，安裝即可離線使用。
2. 安裝後依照 **IMEtro 設定精靈** 於系統設定啟用 **IMEtro**，並切換為目前輸入法。
3. 日後可在「設定 › 關於 IMEtro › 檢查更新」取得 GitHub Releases 的最新版本；若遇到安裝異常或問題，歡迎於 [Issues](https://github.com/milkteaokita/IMEtro/issues) 回報。

> 應用 ID 為 `com.metro.cyime`，與原版 `com.cyletix.cyime` / `com.kingzcheung.xime` 彼此獨立，可共存於同一台裝置而不互相覆蓋資料。

---

## 完整功能一覽

以下標記「預設關」的功能可在 **IMEtro 設定** 中自行開啟。

**中文輸入**
- 全拼 26 鍵、九鍵、14 鍵（基於霧凇拼音詞庫）；九鍵可下載詞組模型與語法增強模型，長句逐鍵選詞。
- 雙拼：內建小鶴、首道、自然碼、智慧 ABC、拼音加加、微軟雙拼，支援複製修改或新建自訂方案（含 14 鍵與九鍵版本，預設關）。
- 筆畫、倉頡五代、倉頡速成、注音大千隨包內建；五筆 86、五筆 98 可於「擴充 › 方案」下載。
- 支援繁體輸出（台灣用語，預設關）、模糊音、生僻字、中英混輸、中文滑行輸入；26 鍵支援智慧糾錯且不搶佔首位候選。
- 詞庫管理：個人短語、匯入 TXT／搜狗／QQ／Rime 詞庫、管理已刪候選、查詞與分類詞庫。

**多語言支援**
- **英文**：整詞補全與滑行輸入。
- **日語**：羅馬字 26 鍵與假名拂動鍵盤（基於 rime-jaroomaji）。
- **其他語言**：西語、德語、法語、義大利語、俄語、韓語、越南語、馬來語專用鍵盤與補全詞表（預設關）。

**手寫、語音與拍照識字**
- **手寫**：支援中／英／日多字連寫與數學公式轉 LaTeX，支援 S Pen。
- **語音**：預設離線 SenseVoice（自動標點、整句定稿），另可選串流 Paraformer、Zipformer、X-ASR 與日語模型，或自訂線上辨識介面。
- **拍照 OCR**：於剪貼簿中拍照或選取圖片，離線擷取文字。

**即時字幕與無障礙**
- 支援中／英／日即時字幕、講者自動分類與命名、背景浮窗、通話字幕、3 天本機紀錄搜尋分享與點名震動提醒；桌面提供獨立「字幕」捷徑。

**鍵盤外觀、剪貼簿與擴充**
- **擺放模式**：固定、懸浮（自由拖曳／縮放／旋轉）、分體鍵盤；平板固定鍵盤可由頂邊把手向上拉成懸浮。
- **字母排列**：QWERTY、Colemak、QWJRTK、Cyletix10、六邊形蜂巢排列，以及自訂鍵盤編輯器。
- **工具列與剪貼簿**：支援 Live Tile 動態磚選單與拖曳排序；剪貼簿歷史、釘選、搜尋、自訂分組、匯出（TXT / Markdown / JSON）、簡訊驗證碼自動擷取與 Windows 配對同步。
- **擴充與設定**：設定頁全文搜尋（支援同義詞與繁簡互通）、本機備份還原、效能檔位調整，以及支援 Lua 插件（`.xipk`）市場。

---

## 從原始碼建置

```bash
# 複製專案（含子模組）
git clone --recursive https://github.com/milkteaokita/IMEtro.git
cd IMEtro

# 若已複製專案，初始化子模組
git submodule update --init --recursive

# 建置 Debug / Release APK
./gradlew assembleDebug
./gradlew assembleRelease

# 高敏感離線版（移除 INTERNET 權限）
./gradlew assembleOffline
```

- 建置環境需求：**JDK 17 / 21** 與 Android NDK（專案設定於 Gradle / SDK 版本清單）。
- 技術堆疊：Kotlin · Jetpack Compose · Metro Design System · librime (JNI / C++) · ONNX Runtime · sherpa-onnx · OpenCV。
- 原始碼內部命名空間沿用上游 `com.kingzcheung.xime`，對外安裝套件名為 `com.metro.cyime`。

---

## 致謝

**IMEtro** 站在以下優秀開源專案、詞庫、語料與模型的基礎上完成，謹此向所有原作者與貢獻者致謝；完整授權條款見倉庫 [LICENSE](LICENSE)、`app/src/main/assets/NOTICE.txt` 與應用內「設定 › 關於 IMEtro › 致謝／開源授權條款」。

| 專案 | 作者 | 說明 | 授權條款 |
| --- | --- | --- | --- |
| [**CyIME**](https://github.com/Cyletix/CyIME) | Cyletix | IMEtro 上游專案（完整輸入引擎與多模態基礎） | GPL-3.0 |
| [**Xime**](https://github.com/ximeiorg/Xime) | Kingz Cheung | CyIME 上游原始專案 | GPL-3.0 |
| [**metro-home**](https://github.com/milkteaokita) | — | Metro 視覺語彙與動態系統設計參考 | — |

<details>
<summary>完整依賴與資料模型清單</summary>

### 輸入引擎與基礎庫
- [librime](https://github.com/rime/librime)（BSD-3-Clause）、[librime-lua](https://github.com/hchunhui/librime-lua)（BSD-3-Clause）、[librime-octagram](https://github.com/lotem/librime-octagram)（GPL-3.0）、[librime-predict](https://github.com/rime/librime-predict)（BSD-3-Clause）、[OpenCC](https://github.com/BYVoid/OpenCC)（Apache-2.0）
- Kotlin / Jetpack Compose / AndroidX / OkHttp / Coil / kaml / ZXing / exp4j / Apache Commons Compress（Apache-2.0）、LuaJ（MIT）、sora-editor（LGPL-2.1）

### 詞庫、方案與釋義
- [霧凇拼音 rime-ice](https://github.com/iDvel/rime-ice)（GPL-3.0）、[萬象語言模型 RIME-LMDG](https://github.com/amzxyz/RIME-LMDG)（CC BY 4.0）
- [rime-wubi](https://github.com/kingzcheung/rime-wubi)（Apache-2.0）、[rime-wubi98](https://github.com/cz-archive/rime-wubi98)（GPL-3.0）、[rime-stroke](https://github.com/rime/rime-stroke)（LGPL-3.0）
- [Cangjie5](https://github.com/Jackchows/Cangjie5)（MIT）、[小麥注音 McBopomofo](https://github.com/openvanilla/McBopomofo)（MIT）、[rime-jaroomaji](https://github.com/lazyfoxchan/rime-jaroomaji)（Apache-2.0）
- [ECDICT](https://github.com/skywind3000/ECDICT)（MIT）、[CC-CEDICT](https://www.mdbg.net/chinese/dictionary?page=cc-cedict)（CC BY-SA 4.0）、[ChaiPUA 字型](https://github.com/tansongchen/input)

### 語音、手寫、OCR 與模型
- [sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx)（Apache-2.0）、[ONNX Runtime](https://github.com/microsoft/onnxruntime)（MIT）、[Silero VAD](https://github.com/snakers4/silero-vad)（MIT）
- [SenseVoice](https://github.com/FunAudioLLM/SenseVoice)、[Paraformer](https://github.com/modelscope/FunASR)、[3D-Speaker](https://github.com/modelscope/3D-Speaker)
- [ochwpro](https://www.modelscope.cn/models/bikeand/ochwpro)（MIT）、[PaddleOCR PP-OCRv5](https://github.com/PaddlePaddle/PaddleOCR) / [RapidOCR](https://github.com/RapidAI/RapidOCR)（Apache-2.0）、[OpenCV](https://github.com/opencv/opencv)（Apache-2.0）、[KaTeX](https://github.com/KaTeX/KaTeX)（MIT）
- [slimt](https://github.com/jerinphilip/slimt)（GPL-2.0）、[Firefox Translations](https://github.com/mozilla/translations)（MPL-2.0）、[LCCC](https://github.com/thu-coai/CDial-GPT)（MIT）、[MathWriting](https://github.com/google-research/google-research/tree/master/mathwriting)（CC BY-NC-SA 4.0）

</details>

---

## 授權條款

本專案採用 **[GNU GPLv3](LICENSE)** 開源授權（衍生自 [CyIME](https://github.com/Cyletix/CyIME) 與 [Xime](https://github.com/ximeiorg/Xime)）。所有原始檔案版權聲明皆依授權保留；第三方元件與模型依各自授權條款釋出（請注意部分非商業授權模型如 `predictive-text-base` 與 MathWriting 訓練集不得用於商業用途）。
