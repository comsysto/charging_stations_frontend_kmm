plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.android.library")
    id("kotlinx-serialization")
    id("app.cash.sqldelight") version "2.0.0"
}

kotlin {
    android {
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
        val ktorVersion = "2.3.4"
        val sqlDelightVersion = "2.0.0"

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.0-alpha05")
                implementation("com.squareup.okio:okio:3.3.0")
                implementation("io.ktor:ktor-client-core:$ktorVersion")

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
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
//        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies{
                implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
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
    databases {
        create("StationsDatabase") {
            packageName.set("com.emobilitychargingstations.database")
        }
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    kotlin {
        jvmToolchain(17)
    }

    namespace = "com.comsystoreply.emobilitychargingstations"
    compileSdk = 34
    defaultConfig {
        minSdk = 31
        targetSdk = 33
    }
}
