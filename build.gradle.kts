import com.google.protobuf.gradle.id
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("com.avast.gradle.docker-compose") version "0.17.12"
    id("com.google.protobuf") version "0.9.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.3.3"
    id("org.springframework.boot") version "3.4.2"
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.allopen") version "2.1.10"
    kotlin("plugin.jpa") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
}

group = "net.synaptology"
version = "0.5.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

val agent = configurations.create("agent")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-parent:3.4.3")
    implementation("org.springframework.boot:spring-boot-maven-plugin:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("net.devh:grpc-spring-boot-starter:3.1.0.RELEASE")
    implementation("com.google.protobuf:protobuf-java:4.29.3")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-netty")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.4")
    // Data persistence
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    // OTEL
    implementation("io.opentelemetry:opentelemetry-api")
    agent("io.opentelemetry.javaagent:opentelemetry-javaagent:2.13.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.grpc:grpc-testing:1.70.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    testImplementation("org.testcontainers:testcontainers:1.20.5")
    testImplementation("org.testcontainers:junit-jupiter:1.20.5")
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
        mavenBom("io.grpc:grpc-bom:1.70.0")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.70.0"
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
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.1")
    byteBuddyAgent("net.bytebuddy:byte-buddy-agent:1.17.1") { isTransitive = false }
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
    tcpPortsToIgnoreWhenWaiting.addAll(8888, 13133)
}

val copyAgent = tasks.register<Copy>("copyAgent") {
    from(agent.singleFile)
    into(layout.buildDirectory.dir("agent"))
    rename("opentelemetry-javaagent-.*\\.jar", "opentelemetry-javaagent.jar")
}

tasks.named<BootJar>("bootJar") {
    dependsOn(copyAgent)
}

tasks.named<BootRun>("bootRun") {
    dependsOn(copyAgent)

    environment(
        mutableMapOf(
            "OTEL_METRIC_EXPORT_INTERVAL" to 5000,
            "OTEL_METRIC_EXPORT_TIMEOUT" to 10000,
            "OTEL_TRACES_EXPORTER" to "none"
        )
    )

    jvmArgs(
        "-javaagent:${project.projectDir}/build/agent/opentelemetry-javaagent.jar",
    )
}
