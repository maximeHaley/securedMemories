plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.securedmemories"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.securedmemories"
        minSdk = 24
        targetSdk = 35
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
    implementation(libs.androidx.biometric)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.gms:play-services-location:21.0.1")//bug sur la version catalog donc je l'ai import comme ça
    implementation("com.github.bumptech.glide:glide:4.16.0")//bug sur la version catalog donc je l'ai import comme ça
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")//bug sur la version catalog donc je l'ai import comme ça
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
