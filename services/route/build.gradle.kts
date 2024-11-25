@file:Suppress("UnstableApiUsage")


plugins {
    // id("highload.jdbc-e2e-test")
    id("highload.application")
    id("highload.reactive-db")
    id("highload.e2e-test")
    id("highload.security")
    id("highload.common")
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
    implementation(project(":shared:api"))
    implementation(project(":shared:security"))

    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.2")

    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")

    implementation("org.slf4j:slf4j-api")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-logging")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-hystrix:2.2.10.RELEASE")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:4.2.1")
}

highloadApp {
    serviceName.set("route")
}
