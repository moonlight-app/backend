plugins {
    java
    `kotlin-dsl`
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}