import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "se.hb"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.2"
val slf4jVersion = "1.7.32"
val junitJupiterVersion = "5.8.2"
val jacksonVersion = "2.13.1"
val lombokVersion = "1.18.22"

val mainVerticleName = "se.hb.vertxstockbroker.Application"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))

  implementation("io.vertx:vertx-rx-java3")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-config-yaml")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")

  // Database
  implementation("org.flywaydb:flyway-core:8.2.3")
  implementation("org.postgresql:postgresql:42.3.1")
  implementation("io.vertx:vertx-pg-client:4.2.2")

  implementation("io.vertx:vertx-sql-client-templates:4.2.2")
  implementation("io.vertx:vertx-mysql-client:4.2.2")
  implementation("mysql:mysql-connector-java:8.0.25")

  implementation("io.netty:netty-all:4.1.72.Final")

  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  implementation("org.projectlombok:lombok:$lombokVersion")

  implementation("org.slf4j:slf4j-api:$slf4jVersion")
  implementation("org.slf4j:slf4j-simple:$slf4jVersion")

  // Test
  testImplementation("io.vertx:vertx-junit5-rx-java3")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testImplementation("org.mockito:mockito-all:1.10.19")

  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
