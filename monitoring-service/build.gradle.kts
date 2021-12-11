import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "se.hb"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.1"
val junitJupiterVersion = "5.8.1"
val lombokVersion = "1.18.22"
val jacksonVersion = "2.13.0"
val slf4jVersion = "1.7.32"

val applicationName = "se.hb.monitoringservice.Application"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation("io.vertx:vertx-rx-java3:$vertxVersion")
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-web-client:$vertxVersion")
  implementation("io.vertx:vertx-mysql-client:$vertxVersion")
  implementation("io.netty:netty-all:4.1.71.Final")

  implementation("org.projectlombok:lombok:$lombokVersion")
  implementation("org.slf4j:slf4j-api:$slf4jVersion")
  implementation("org.slf4j:slf4j-simple:$slf4jVersion")
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

  testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")

  compileOnly("org.projectlombok:lombok:$lombokVersion")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events = setOf(PASSED, SKIPPED, FAILED)
    }
  }

  withType<JavaExec> {
    args = listOf(
      "run",
      applicationName,
      "--redeploy=$watchForChange",
      "--launcher-class=$launcherClassName",
      "--on-redeploy=$doOnChange"
    )
  }

  shadowJar {
    archiveClassifier.set("fat")
    manifest {
      attributes(mapOf("Main-Verticle" to applicationName))
    }
    mergeServiceFiles()
  }
}

