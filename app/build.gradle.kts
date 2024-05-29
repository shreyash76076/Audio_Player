plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.audioplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.audioplayer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    dataBinding{
        enable= true
    }
    buildFeatures {
        viewBinding =true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //audioPlayer
    implementation ("androidx.media:media:1.6.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.18.7")
    implementation ("com.google.android.exoplayer:extension-mediasession:2.18.7")

    implementation(project(":kotlin-audio"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.glide)
    implementation (libs.glide.transformations)
    implementation(libs.kotlinx.serialization.json)
    implementation (libs.kotlinx.serialization.core)
//    implementation(project(":kotlin-audio"))


}