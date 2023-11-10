plugins {
    kotlin("android").version("1.9.0").apply(false)
    kotlin("multiplatform").version("1.9.0").apply(false)
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {setUrl("https://jitpack.io")}
    }
    dependencies {
        classpath("com.android.application:com.android.application.gradle.plugin:8.1.1")
        classpath("com.android.library:com.android.library.gradle.plugin:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.5")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {setUrl("https://jitpack.io") }
    }
}

//plugins {
//    //trick: for the same plugin versions in all sub-modules
//    id("com.android.application").version("7.4.0").apply(false)
//    id("com.android.library").version("7.4.0").apply(false)
//    kotlin("android").version("1.8.0").apply(false)
//    kotlin("multiplatform").version("1.8.0").apply(false)
//}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}
