import com.google.protobuf.gradle.id
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.avast.gradle.docker-compose") version "0.17.12"
    id("com.google.protobuf") version "0.9.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.1.0"
    id("org.springframework.boot") version "3.4.1"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
}

group = "net.synaptology"
version = "0.3.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-parent:3.4.1")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.4.1")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("com.google.protobuf:protobuf-java:4.29.1")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-netty")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    // Data persistence
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.grpc:grpc-testing:1.69.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STARTED
        )
        showStandardStreams = true // Includes TestLogEvent.STANDARD_OUT and TestLogEvent.STANDARD_ERROR
    }
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("io.grpc:grpc-bom:1.68.2")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.2"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach { generateProtoTask ->
            generateProtoTask
                .plugins {
                    id("grpc")
                    id("grpckt")
                }
        }
    }
}

val byteBuddyAgent = configurations.create("byteBuddyAgent")

dependencies {
    testImplementation("net.bytebuddy:byte-buddy-agent:1.15.11")
    byteBuddyAgent("net.bytebuddy:byte-buddy-agent:1.15.11") { isTransitive = false }
}

tasks {
    test {
        jvmArgs("-javaagent:${byteBuddyAgent.asPath}")
    }
}

// Refer to Persistence with JPA:
//   https://spring.io/guides/tutorials/spring-boot-kotlin#_persistence_with_jpa
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dockerCompose {
    useComposeFiles.add("docker/dev/docker-compose.yml")
}
