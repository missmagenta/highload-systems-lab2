plugins {
    id("highload.reactive-db")
    id("highload.web")
    id("highload.application")
    id("highload.security")
    id("highload.e2e-test")

    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

highloadApp {
    serviceName.set("authentication")
}

testing {
    suites {
        val integrationTest by getting(JvmTestSuite::class) {
            dependencies {
                implementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.1.4")
            }
        }
    }
}

dependencies {
    implementation(project(":shared:security"))
    implementation(project(":shared:api"))

    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
}
