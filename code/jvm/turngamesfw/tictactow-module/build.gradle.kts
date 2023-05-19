plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    java
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fw-interfaces-module"))

    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}