# 📱 JH Tone - Web to APK

Auto-build Android APK from **https://jh.auth.jhtone.site/** using GitHub Actions.

---

## 🚀 Quick Setup (5 minutes)

### Step 1 — Upload to GitHub

```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

### Step 2 — GitHub Actions Auto-Runs

After pushing, go to your repo → **Actions** tab → watch the build run automatically.

✅ APK will be in the **Artifacts** section after ~3 minutes.

---

## 🔐 Signed Release APK (Optional)

To create a properly signed APK, add these **Repository Secrets**:

Go to: `Settings → Secrets and variables → Actions → New repository secret`

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | Base64 of your `.jks` keystore file |
| `KEY_STORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

### Generate a Keystore

```bash
keytool -genkeypair -v \
  -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -alias release

# Convert to base64 for the secret:
base64 -w 0 my-release-key.jks
```

> ⚠️ **Without secrets**: A temporary keystore is auto-generated (debug-signed APK, works fine for testing).

---

## 📦 Create a GitHub Release (with APK attached)

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions will:
1. Build the APK
2. Create a Release page automatically
3. Attach the `.apk` file to the release

---

## ✨ App Features

| Feature | Details |
|---|---|
| 🌐 URL | https://jh.auth.jhtone.site/ |
| 🎨 Icon | Auto-downloaded from Cloudinary |
| ⚡ Engine | Android WebView (hardware accelerated) |
| 🔄 Refresh | Pull-to-refresh gesture |
| 📶 Offline | Custom offline error page |
| 🌟 Splash | Animated splash screen (2s) |
| 🔙 Back nav | Hardware back button navigation |
| 📱 Min SDK | Android 5.0 (API 21) |
| 🎯 Target SDK | Android 14 (API 34) |

---

## 📁 Project Structure

```
webtoapp/
├── .github/
│   └── workflows/
│       └── build-apk.yml        ← GitHub Actions workflow
├── app/
│   ├── src/main/
│   │   ├── java/com/webtoapp/
│   │   │   ├── MainActivity.java      ← WebView app
│   │   │   └── SplashActivity.java    ← Splash screen
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   └── activity_splash.xml
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       └── styles.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── generate_icons.py             ← Icon generator script
├── gradle/wrapper/
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## 🔧 Manual Build (local)

```bash
# Generate icons first
python3 generate_icons.py

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

APK output: `app/build/outputs/apk/`
