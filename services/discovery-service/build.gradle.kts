plugins {
    java
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
    `spring-service`
}

val springCloudVersion by extra("2024.0.0")

group = "ru.moonlightapp.backend"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springframework.cloud.spring.cloud.starter.netflix.eureka.server)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}