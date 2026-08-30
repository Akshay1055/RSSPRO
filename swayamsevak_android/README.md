# स्वयंसेवक — Native Android MVP

यह Flutter के बिना बनाया गया **native Android Studio + Kotlin + Jetpack Compose** project है।

## आवश्यकताएँ

- Android Studio
- Android SDK
- JDK 17 (Android Studio bundled JDK सामान्यतः पर्याप्त है)
- इंटरनेट पहली Gradle sync/build के लिए आवश्यक हो सकता है

Flutter/Dart की आवश्यकता नहीं है।

## Android Studio में खोलें

1. ZIP extract करें.
2. Android Studio → File → Open.
3. `swayamsevak_android` folder चुनें.
4. Gradle Sync पूरा होने दें.
5. Device/emulator चुनें.
6. Run ▶ दबाएँ.

## APK

Android Studio:
Build → Generate App Bundles or APKs → Generate APK

या Terminal:

```bash
gradlew assembleDebug
```

Release:

```bash
gradlew assembleRelease
```

अगर Windows में Gradle wrapper पहली बार उपलब्ध न हो, Android Studio का Gradle Sync पूरा करें।

Debug APK सामान्यतः:
`app/build/outputs/apk/debug/app-debug.apk`

Release APK:
`app/build/outputs/apk/release/app-release.apk`

## Demo

Welcome screen:
- स्वयंसेवक पंजीयन
- Admin Demo
- DeveloperAdmin Demo

OTP screen में real SMS नहीं जाता; यह UI/demo flow है।

## Production note

यह V1 mobile application है। अभी backend/API/PostgreSQL connected नहीं है।

Production architecture:
Android Kotlin → REST API → Backend → PostgreSQL

Web Admin Panel अलग web project के रूप में जोड़ा जाएगा।

## Prompt coverage

यह MVP आपके दिए Master Prompt के अनुसार registration-first approach, organizational hierarchy, role/permission concepts, DeveloperAdmin field management और secondary attendance/poll modules का mobile implementation देता है।
