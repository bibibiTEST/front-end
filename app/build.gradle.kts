plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.upload"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.upload"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.foundation.android)
    implementation("com.squareup.okio:okio:3.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // 如果需要 OkHttp
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")//用于自动刷新
    implementation ("com.github.bumptech.glide:glide:4.15.1")  // 添加 Glide 依赖
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")  // 添加 Glide 编译器
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}