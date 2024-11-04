import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    // google-service
    id("com.google.gms.google-services")
    // firebase crashlytics
    id("com.google.firebase.crashlytics")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("secret.properties")))
}

android {
    namespace = "com.glion.skinscanner_and"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.glion.skinscanner_and"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_MAP_KEY", properties.getProperty("KAKAO_MAP_KEY"))
        buildConfigField("String", "KAKAO_REST_KEY", properties.getProperty("KAKAO_REST_KEY"))
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
        // 보상형 광고 App Id manifestPlaceholders 로 저장
        manifestPlaceholders["AD_APP_ID"] = properties.getProperty("REWARD_AD_APP_ID") as String
        // 보상형 광고 ID buldConfigField 에 저장
        buildConfigField("String", "AD_ID", properties.getProperty("REWARD_AD_ID"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }

    androidResources {
        ignoreAssetsPattern = "tflite" // tflite 압축하지 않은 상태로 저장할 자산으로 지정
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // cameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // tensorflow lite
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.select.tf.ops)
    // tensorflow lite support library
    implementation(libs.tensorflow.lite.v000nightlysnapshot)
    // The GPU delegate library is optional. Depend on it as needed.
    implementation (libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite.support.v044)

    // Glide
    implementation(libs.glide)

    // Image Cropper
    implementation(libs.vanniktech.android.image.cropper)

    // KakaoSdk
    implementation(libs.v2.all) // 전체 모듈 설치, 2.11.0 버전부터 지원
    // KakaoMap Sdk
    implementation(libs.android)

    // Retrofit2
    implementation(libs.retrofit)

    // Google play service location
    implementation(libs.play.services.location)
    // AdMob
    implementation(libs.play.services.ads)

    // firebase bom
    implementation(platform(libs.firebase.bom))
    // firebase crashlytics
    implementation(libs.firebase.crashlytics)
    // firebase analytics
    implementation(libs.firebase.analytics)
    // firebase realtime database
    implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}