import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.moonlight"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation(libs.commonsIo)
    implementation(libs.jjwtImpl)
    implementation(libs.jjwtJackson)
    implementation(libs.springdocOpenapi)

    annotationProcessor(libs.jpaModelgen)

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
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

tasks.build {
    finalizedBy("copyDependencies")
}