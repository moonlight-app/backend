plugins {
    java
}

var libs: VersionCatalog by extra(versionCatalogs.named("libs"))

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
    mavenLocal()
}

dependencies {
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())

    testImplementation(libs.findLibrary("springframework-spring-boot-starter-test").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-junit-platform-launcher").get())
}

tasks.named("bootJar") {
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