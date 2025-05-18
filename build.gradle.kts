plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"

    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("plugin.serialization") version "1.8.0"

    // 엔티티 클래스 기본 생성자 생성
    kotlin("plugin.jpa") version "1.9.25"

    // kotlin에서 lombok 사용이 가능해지게 만들어주는 플러그인
    kotlin("plugin.lombok") version "1.9.25"
    id("io.freefair.lombok") version "8.1.0"

    // 코틀린에서 클래스와 메소드가 기본적으로 'final'인 문제 해결
    kotlin("plugin.allopen") version "2.1.20"
}

group = "scale"
version = "0.0.1-SNAPSHOT"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
    apply(plugin = "io.freefair.lombok")

    allOpen {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.MappedSuperclass")
        annotation("jakarta.persistence.Embeddable")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("ch.qos.logback:logback-classic")
        implementation ("io.github.microutils:kotlin-logging:3.0.5")

        // Lombok 의존성
        compileOnly("org.projectlombok:lombok:1.18.30")
        annotationProcessor("org.projectlombok:lombok:1.18.30")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation(kotlin("test"))

        testImplementation("io.mockk:mockk:1.13.8")
        testImplementation("io.kotest:kotest-runner-junit5:5.8.0")

        // 테스트 코드에서도 @Slf4j 사용 가능하게
        testCompileOnly("org.projectlombok:lombok:1.18.30")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}