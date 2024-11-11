plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "highload"

include(
    "services:place",
    "services:route",
    "services:feedback",
    "services:favorites",
    "services:api-gateway",
    "services:authentication",
    "services:cloud-config",
    "services:eureka-server",
    "shared:security",
    "shared:web-security",
    "shared:webflux-security",
    "shared:api",
    "shared:db-migrations",
    "shared:integration-tests",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("services:favorites")
findProject(":services:favorites")?.name = "favorites"
include("services:favorites")
findProject(":services:favorites")?.name = "favorites"
