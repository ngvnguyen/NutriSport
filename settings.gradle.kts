enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NutriSport"
include(":androidApp")
include(":data")
include(":di")
include(":feature:auth")
include(":feature:home")
include(":feature:profile")
include(":navigation")
include(":shared")
