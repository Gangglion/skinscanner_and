// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    // google-service
    alias(libs.plugins.gms.google.services) apply false
    // firebase crashlytics
    alias(libs.plugins.firebase.crashlytics) apply false
    // hilt
    alias(libs.plugins.hilt.android) apply false
}