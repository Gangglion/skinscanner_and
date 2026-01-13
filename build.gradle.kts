// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Plugin.ANDROID_APPLICATION) version Versions.AGP apply false
    id(Plugin.JETBRAINS_KOTLIN_ANDROID) version Versions.KOTLIN apply false
    id(Plugin.ANDROIDX_NAVIGATION_SAFEARGS) version Versions.NAVIGATION_SAFEARG_GRADLE_PLUGIN apply false
    id(Plugin.GMS_GOOGLE_SERVICES) version Versions.GMS_GOOGLE_SERVICES apply false
    id(Plugin.FIREBASE_CRASHLYTICS) version Versions.FIREBASE_CRASHLYTICS apply false
    id(Plugin.HILT_ANDROID) version Versions.HILT_ANDROID apply false
}