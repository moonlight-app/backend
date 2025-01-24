plugins {
    java
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
    `spring-service`
}

val springCloudVersion by extra("2024.0.0")

group = "ru.moonlightapp.backend"
version = "0.0.1"

dependencies {
    implementation(libs.moonlightapp.backend.core)

    implementation(libs.springframework.spring.boot.starter.data.jpa)
    implementation(libs.springframework.spring.boot.starter.security)
    implementation(libs.springframework.spring.boot.starter.validation)
    implementation(libs.springframework.spring.boot.starter.web)

    implementation(libs.springframework.cloud.spring.cloud.starter.netflix.eureka.client)

    implementation(libs.commons.io)
    implementation(libs.springdoc.openapi)

    annotationProcessor(libs.hibernate.jpamodelgen)

    runtimeOnly(libs.h2database.h2)
    runtimeOnly(libs.postgresql.postgresql)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}