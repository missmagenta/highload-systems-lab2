@file:Suppress("UnstableApiUsage")


plugins {
    id("highload.web")
    id("highload.application")
    id("highload.reactive-db")
    id("highload.security")
    // id("highload.e2e-test")

    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":shared:api"))
    implementation(project(":shared:security"))

    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:4.2.1")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    @Suppress("VulnerableDependency")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-hystrix:2.2.10.RELEASE")

}

highloadApp {
    serviceName.set("feedback")
}
