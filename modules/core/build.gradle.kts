import com.google.protobuf.gradle.id

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.google.protobuf)
}

val springCloudVersion by extra("2024.0.0")

group = "ru.moonlightapp.backend"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springframework.data.spring.data.jpa)
    implementation(libs.springframework.security.spring.security.core)
    implementation(libs.hibernate.orm.hibernate.core)

    api(libs.devh.grpc.client.spring.boot.starter)
    api(libs.devh.grpc.server.spring.boot.starter)
    api(libs.grpc.all)
    api(libs.google.protobuf.java)
    api(libs.javax.annotation)

    api(libs.commons.io)
    api(libs.springdoc.openapi)

    annotationProcessor(libs.hibernate.jpamodelgen)

    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.google.protobuf.java.get()}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") {}
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.build {
    finalizedBy(tasks.publishToMavenLocal)
}