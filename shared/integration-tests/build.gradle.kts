@file:Suppress("UnstableApiUsage")

plugins {
    id("highload.common")
    id("highload.web")
    `java-test-fixtures`
    java
}

dependencies {
    implementation("io.rest-assured:rest-assured")
    implementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:junit-jupiter")
    implementation("org.springframework.boot:spring-boot-testcontainers")
    implementation("org.testcontainers:mongodb:1.18.3")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:1.35")

    implementation("org.springframework.boot:spring-boot-testcontainers")
    testFixturesImplementation(project(":shared:api"))
}

sourceSets {
    main {
        resources {
            resources.setSrcDirs(files("src/main/non-existent"))
        }
    }
}
