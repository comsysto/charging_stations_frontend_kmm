plugins {
    val kotlinVersion = "1.9.20"
    kotlin("android").version("$kotlinVersion").apply(false)
    kotlin("native.cocoapods").version("$kotlinVersion")
    kotlin("multiplatform").version("$kotlinVersion").apply(false)
    kotlin("jvm") version "$kotlinVersion"
    kotlin("plugin.serialization") version "$kotlinVersion"
    id("app.cash.sqldelight") version "2.0.0"
}
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {setUrl("https://jitpack.io")}
    }
    dependencies {
        classpath("com.android.application:com.android.application.gradle.plugin:8.1.2")
        classpath("com.android.library:com.android.library.gradle.plugin:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("com.android.tools.build:gradle:8.1.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {setUrl("https://jitpack.io") }
    }
}
