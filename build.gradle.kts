import io.quarkus.gradle.tasks.QuarkusNative
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.monster"
version = "1.0.0-SNAPSHOT"

val quarkusPlatformVersion = "1.3.1.Final"

plugins {
    id("io.quarkus")
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.allopen")
}

repositories {
     mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Quarkus Extensions
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy")
    // https://github.com/quarkusio/quarkus/issues/6041
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.2")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-resteasy-mutiny")

    // Project Reactor
    implementation(platform("io.projectreactor:reactor-bom:Bismuth-RELEASE"))
    implementation("io.projectreactor:reactor-core")
    implementation("io.smallrye.reactive:mutiny-reactor:0.4.3")

    // Vert.x Mutiny Web client
    implementation("io.quarkus:quarkus-vertx-web")
    implementation("io.vertx:vertx-web-client:3.8.5")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-web-client")

    // Amazon AWS SDK
    implementation("software.amazon.awssdk:sns:2.10.71")
    implementation("software.amazon.awssdk:sqs:2.11.14")
    runtimeOnly("software.amazon.awssdk:apache-client:2.10.70")
    implementation("software.amazon.awssdk:netty-nio-client:2.10.70")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.5")


    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation("io.arrow-kt:arrow-syntax:0.10.4")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:testcontainers:1.13.0")
    testImplementation("org.testcontainers:junit-jupiter:1.13.0")
    testImplementation("org.testcontainers:localstack:1.13.0")
    testImplementation("com.amazonaws:aws-java-sdk:1.11.756")
    testImplementation("org.projectlombok:lombok:1.18.12")

}

quarkus {
    setOutputDirectory("$projectDir/build/classes/kotlin/main")
}

tasks {
    named<QuarkusNative>("buildNative") {
        isEnableHttpUrlHandler = true
    }
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
