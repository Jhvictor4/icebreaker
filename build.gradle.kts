import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private val ktor_version = "2.3.8"

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jlleitschuh.gradle.ktlint") version "11.2.0"
    id("application")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.wafflestudio.ai"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_19
}

application {
    applicationDefaultJvmArgs += listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("software.amazon.awssdk:secretsmanager:2.17.276")
    implementation("software.amazon.awssdk:sts:2.17.276")
    implementation("io.weaviate:client:4.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2")

    implementation("com.aallam.openai:openai-client:3.6.3")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    implementation("com.google.zxing:javase:3.5.3")
    implementation("com.google.zxing:core:3.5.3")

    implementation("io.asyncer:r2dbc-mysql:1.1.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

ktlint {
    disabledRules.set(
        setOf("no-wildcard-imports", "enum-entry-name-case")
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs += "-Xcontext-receivers"
        jvmTarget = "19"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
