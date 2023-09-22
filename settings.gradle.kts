pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

//dependencyResolutionManagement {
//    repositories {
//        google()
//        mavenCentral()
//    }
//}

rootProject.name = "EMobility_Charging_Stations"
include(":androidApp")
include(":shared")
include(":automotive")
