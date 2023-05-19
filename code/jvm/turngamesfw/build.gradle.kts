import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
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
