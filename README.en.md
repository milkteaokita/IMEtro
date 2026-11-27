<h1 align="center">IMEtro</h1>

<p align="center">
  <strong>Offline Android Input Method Redesigned in Authentic Windows Phone Metro Style</strong><br/>
  <a href="README.md">繁體中文</a> · <a href="README.zh-CN.md">简体中文</a>
</p>

**IMEtro** (v2.0.0) is an offline-capable Android IME built on [CyIME](https://github.com/Cyletix/CyIME) (forked from [Xime](https://github.com/ximeiorg/Xime)) and the [Rime](https://rime.im/) engine, completely redesigned in classic **Windows Phone 7 / 8.1 / 10 Mobile Metro** aesthetics. Every screen—settings, setup wizard, keyboard panels, Live Tile menus, clipboard, and live captions—adopts crisp `0dp` right-angle geometry, pure `#000000` / `#FFFFFF` high-contrast OLED surfaces, Metro typography, and live theme synchronization.

---

## Metro Design Highlights

- **100% Authentic Metro UI**: Pure `0dp` rectangles (`RectangleShape`), zero drop shadows, high-contrast OLED surfaces, custom Metro toggles/sliders/dialogs, and no legacy rounded cards or Material chrome.
- **Metro Era Modes**: Switch between **Early WP7**, **Mid WP8.1**, and **Late W10M** typography scales and active Pivot indicators.
- **14 Metro Accents & OS Live Sync**: Built-in Zune Magenta, Cobalt, Metro Cyan, and 11 more Metro accents, with live accent/dark-mode synchronization from Metro Launcher (`com.metro.launcher`).
- **Signature Metro Motion**: 5-tab **Metro Pivot** settings home, **3D Turnstile** page transitions, and **3D Tilt** touch feedback on tiles and rows (zero latency on fast-typing letter/number keys), plus a **Metro Live Tile** keyboard menu grid.
- **Offline-First Multi-Modal Input**: Local Rime dictionaries, multi-character handwriting & LaTeX formula recognition, on-device speech-to-text (SenseVoice / Paraformer / Zipformer / X-ASR), offline photo OCR, and real-time accessibility captions.

---

## Download & Install

Requires Android 9.0 (API 28) or later.

1. Download the APK from [**milkteaokita/IMEtro Releases**](https://github.com/milkteaokita/IMEtro/releases):
   - **Standard / Lite** `IMEtro-<version>-arm64-v8a.apk` (or `-universal.apk`): download voice, handwriting, and prediction models in *Extensions › Models* when needed.
   - **Full** `IMEtro-<version>-full-arm64-v8a.apk`: bundles offline voice, handwriting, speaker, and prediction models out of the box.
2. Enable **IMEtro** in system settings and set it as your active keyboard.
3. Check for future updates under *Settings › About IMEtro › Check for updates*, or open [Issues](https://github.com/milkteaokita/IMEtro/issues).

> Package applicationId: `com.metro.cyime` (independent from `com.cyletix.cyime` and `com.kingzcheung.xime`, so they can coexist safely).

---

## Core Features

- **Chinese**: 26-key, 9-key, and 14-key Pinyin (`rime-ice`), 6 built-in & custom Shuangpin layouts, Stroke, Cangjie 5, Quick, Zhuyin, Wubi 86/98, Traditional Chinese (Taiwan) output, fuzzy Pinyin, glide typing, dictionary import & lookup.
- **Multilingual**: English completion/glide, Japanese Romaji & Kana (`rime-jaroomaji`), plus Spanish, German, French, Italian, Russian, Korean, Vietnamese, and Malay keyboards.
- **Handwriting, Voice & OCR**: Multi-character Chinese/English/Japanese & LaTeX math handwriting with S Pen support, offline speech recognition (hold Space to dictate), and clipboard photo OCR.
- **Live Captions**: Real-time subtitles in Chinese/English/Japanese with speaker diarization, call captions, floating overlay, and 3-day local searchable history.
- **Keyboard, Clipboard & Extensions**: Docked, floating (move/resize/rotate), and split modes; QWERTY, Colemak, QWJRTK, Cyletix10, hexagon, and custom layouts; Live Tile toolbar, pinned/grouped clipboard, SMS OTP detection, and `.xipk` Lua plugins.

---

## Build

```bash
git clone --recursive https://github.com/milkteaokita/IMEtro.git
cd IMEtro
git submodule update --init --recursive

./gradlew assembleDebug
./gradlew assembleRelease
./gradlew assembleOffline
```

Tech stack: Kotlin · Jetpack Compose · Metro Design System · librime (JNI / C++) · ONNX Runtime · sherpa-onnx · OpenCV.

---

## Credits & License

Licensed under **[GNU GPLv3](LICENSE)**. Derived from **[CyIME](https://github.com/Cyletix/CyIME)** (by Cyletix) and **[Xime](https://github.com/ximeiorg/Xime)** (by Kingz Cheung), with full upstream copyright notices and third-party attributions preserved in [README.md](README.md), `app/src/main/assets/NOTICE.txt`, and in-app *Settings › About IMEtro › Credits*.
