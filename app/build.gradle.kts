plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.simats.weekcart"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.simats.weekcart"
        minSdk = 23
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Image Loading
    implementation(libs.glide)

    // Security (for token storage)
    implementation(libs.security.crypto)
    
    // Charting
    implementation(libs.mpandroidchart)
    implementation(libs.billing.ktx)

    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}