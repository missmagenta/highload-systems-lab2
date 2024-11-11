plugins {
    id("highload.reactive-db")
    id("highload.web")
    id("highload.application")
    id("highload.security")
}

highloadApp {
    serviceName.set("authentication")
}

dependencies {
    implementation(project(":shared:security"))
    implementation(project(":shared:api"))

    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
}
