import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	java
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("org.springframework.security:spring-security-core:5.7.3")

	// For JDBI
	implementation("org.jdbi:jdbi3-core:3.33.0")
	implementation("org.jdbi:jdbi3-kotlin:3.33.0")
	implementation("org.jdbi:jdbi3-postgres:3.33.0")
	implementation("org.postgresql:postgresql:42.5.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(kotlin("test"))
}

task<Exec>("dbTestsUp") {
	commandLine("docker-compose", "up", "-d", "--build", "--force-recreate", "db-tests")
}

task<Exec>("dbTestsWait") {
	dependsOn("dbTestsUp")
	commandLine("docker", "exec", "db-tests", "/app/bin/wait-for-postgres.sh", "localhost")
}

task<Exec>("dbTestsDown") {
	commandLine("docker-compose", "down")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
