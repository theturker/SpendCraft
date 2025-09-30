pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SpendCraft"

include(":app")

include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:ui")
include(":core:analytics")
include(":core:billing")
include(":core:premium")
include(":core:ai")
include(":core:achievements")
include(":core:notifications")

include(":domain")

include(":data:db")
include(":data:repository")

include(":feature:transactions")
include(":feature:reports")
include(":feature:paywall")
include(":feature:premiumdebug")
include(":feature:ai")
include(":feature:settings")
include(":feature:accounts")
include(":feature:recurrence")
include(":feature:sharing")
include(":feature:dashboard")
include(":feature:notifications")
include(":feature:onboarding")
include(":feature:achievements")

include(":tools:export")
