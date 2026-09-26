# AGENTS.md

## Two independent Android projects (both here, not a monorepo)

- `.` — `IdentityCodeShortcut` (app `com.local.identitycode`, "身份码"). The real project. Pure Java, no Kotlin source.
- `litiaotiao/` — a separate decompiled app ("李跳跳"/"派大星2.4"), obfuscated classes (`p094z`, `p095z0`, …). Own `settings.gradle`, own `gradlew`. Do NOT build it from the root; cd into `litiaotiao/` and use its wrapper.

They differ in toolchain — do not mix commands:

| | Root | litiaotiao |
|---|---|---|
| Gradle | 8.5 | 9.0.0 |
| AGP | 8.2.2 | 8.10.1 |
| Java | 17 | 8 |
| compileSdk / targetSdk | 35 / 35 | 33 / 33 |

## Build (offline is mandatory)

The build has no network access; always pass `--offline`. Dependencies are pinned to a bundled `local-repo/` (Kotlin stdlib jars) plus forced versions in `build.gradle` / `app/build.gradle`.

```powershell
# Root app
.\gradlew.bat assembleDebug --offline              # APK -> app/build/outputs/apk/debug/app-debug.apk
.\gradlew.bat :app:compileDebugJavaWithJavac --offline -q   # fast compile-only check
```

`litiaotiao/` builds with its own wrapper, e.g. `.\gradlew.bat assembleDebug` from inside `litiaotiao/`.

- No tests, no lint/CI. Compiling is the only verification.
- `local.properties` (root, `app/`, `litiaotiao/`) each carry `sdk.dir` and must exist to build; never commit them.

## App architecture (root)

Minimal single-screen launcher. `MainActivity` shows two cards (拼多多 / 淘宝) that deep-link into the target app; long-press a card copies the raw station URL.

- `MainActivity` — the only Activity. Fires the PDD deep link (`pinduoduo://com.xunmeng.pinduoduo/web?url=...`) or the Taobao/菜鸟 H5 URL (with `taobao://` scheme as fallback) into `com.xunmeng.pinduoduo` / `com.taobao.taobao`.
- No accessibility service, no background keep-alive, no foreground service — these were removed. The app just opens the target page and the logged-in account renders the current dynamic 身份码.
- Station codes are hardcoded in `MainActivity`: PDD `A029765297`, Taobao/菜鸟 `420485`.

## Non-build reference artifacts (ignore for builds)

Root-level RE files describing Pinduoduo internals, not build inputs: `pdd-dex-strings.txt`, `pdd-manifest.txt`, `mdkd-*.html`, `tools/scan_pdd_dex.py`, `tools/pdd-parse-request*.json`, `core-lambda-stubs.jar`, `*.apk`, `*.jpg`.
