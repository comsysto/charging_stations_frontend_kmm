@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
}

android {
    namespace = "com.comsystoreply.emobilitychargingstations.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.comsystoreply.emobilitychargingstations.android"
        minSdk = 31
        targetSdk = 33
        versionCode = 11
        versionName = "0.29"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain(17)
    }
}

val composeVersion = "1.5.1"

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.7.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation ("androidx.compose.runtime:runtime-livedata:$composeVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation ("com.github.MKergall:osmbonuspack:6.9.0")

    implementation("androidx.car.app:app:1.2.0")
    implementation("androidx.car.app:app-projected:1.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
}