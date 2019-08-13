
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.2.3"

plugins {
    application
    kotlin("jvm") version "1.3.41"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("org.asciidoctor.convert") version "1.5.3"

}

group = "de.xw.contactapi"
version = "1.0-SNAPSHOT"

ext {
    set("snippetsDir", file("build/generated-snippets"))
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.koin:koin-ktor:2.0.1")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    compile("io.github.microutils:kotlin-logging:1.7.4")

    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.3.RELEASE")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")
    testCompile("org.springframework.restdocs:spring-restdocs-restassured:2.0.3.RELEASE")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
        showExceptions = true
        showCauses = true
        showStandardStreams = true
    }

    outputs.dir(ext["snippetsDir"] as File)
}

tasks.asciidoctor {
    inputs.dir(ext["snippetsDir"] as File)
    dependsOn(tasks.test)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    version = "5.5.1"
}
