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
include(":feature:admin_panel")
include(":feature:admin_panel:manage_product")
include(":feature:auth")
include(":feature:cart")
include(":feature:category")
include(":feature:category:category_search")
include(":feature:details")
include(":feature:home")
include(":feature:products_overview")
include(":feature:profile")
include(":navigation")
include(":shared")
