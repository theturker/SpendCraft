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
include(":core:ui")
include(":core:analytics")

include(":domain")

include(":data:db")
include(":data:repository")

include(":feature:transactions")
include(":feature:reports")
// include(":feature:settings") // App modülünde oluşturuldu

include(":tools:export")

include(":feature:ocr")
include(":core:billing")
include(":sync:cloud")
