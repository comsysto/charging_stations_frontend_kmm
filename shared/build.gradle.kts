plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("kotlinx-serialization")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            binaryOption("bundleId", "com.example.emobilitychargingstations")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation(kotlin("stdlib-common"))
                implementation("com.squareup.sqldelight:runtime:1.5.5")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                implementation("com.squareup.okio:okio:3.3.0")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.squareup.sqldelight:android-driver:1.5.5")
            }
        }
//        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies{
                implementation("com.squareup.sqldelight:native-driver:1.5.5")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

sqldelight {
    database ("StationsDatabase") {
        packageName = "com.comsystoreply.chargingstations.database"
        sourceFolders = listOf("sqldelight")
    }
}

android {
    namespace = "com.comsystoreply.emobilitychargingstations"
    compileSdk = 33
    defaultConfig {
        minSdk = 27
        targetSdk = 33
    }
}