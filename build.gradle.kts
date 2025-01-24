import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
}

val springCloudVersion by extra("2024.0.0")

group = "ru.moonlightapp.backend"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springframework.spring.boot.starter.data.jpa)
    implementation(libs.springframework.spring.boot.starter.mail)
    implementation(libs.springframework.spring.boot.starter.security)
    implementation(libs.springframework.spring.boot.starter.validation)
    implementation(libs.springframework.spring.boot.starter.web)

    implementation(libs.springframework.cloud.spring.cloud.starter.circuitbreaker)
    implementation(libs.springframework.cloud.spring.cloud.starter.gateway)
    implementation(libs.springframework.cloud.spring.cloud.starter.netflix.eureka.client)
    implementation(libs.springframework.cloud.spring.cloud.starter.netflix.eureka.server)

    implementation(libs.commons.io)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)
    implementation(libs.springdoc.openapi)

    annotationProcessor(libs.hibernate.jpamodelgen)

    runtimeOnly(libs.h2database.h2)
    runtimeOnly(libs.postgresql.postgresql)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.springframework.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.junit.platform.launcher)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    archiveBaseName = "moonlight-app"
    archiveClassifier = ""
    archiveVersion = ""
    enabled = true
    manifest {
        attributes["Main-Class"] = "ru.moonlightapp.backend.MoonlightApplication"
    }
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath.get())
    into(layout.buildDirectory.dir("libs/libraries"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.build {
    finalizedBy("copyDependencies")
}