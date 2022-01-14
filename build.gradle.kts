import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("io.spring.dependency-management") version "1.0.1.RELEASE"
  id("com.google.cloud.tools.jib") version "3.1.4"
}

group = "se.hb.udemy"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.1"
val slf4jVersion = "1.7.32"
val junitJupiterVersion = "5.8.2"
val lombokVersion = "1.18.22"
val jacksonVersion = "2.13.0"

val mainVerticleName = "se.hb.udemy.vertxstarter.Application"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation("io.vertx:vertx-rx-java3:$vertxVersion")
  implementation("org.projectlombok:lombok:$lombokVersion")
  implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

  implementation("org.slf4j:slf4j-api:$slf4jVersion")
  implementation("org.slf4j:slf4j-simple:$slf4jVersion")

  //testImplementation("io.vertx:vertx-junit5:$vertxVersion")
  testImplementation("io.vertx:vertx-junit5-rx-java3:$vertxVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")

  compileOnly("org.projectlombok:lombok:$lombokVersion")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16
}

jib {
  from {
    image = "adoptopenjdk:16-jre-hotspot"
  }
  to {
    image = "example/jib/vertx-starter"
  }
  container {
    mainClass = "io.vertx.core.Launcher"
    args = listOf("run", mainVerticleName)
    ports = listOf("8888")
  }
}

tasks {
  test {
    useJUnitPlatform()
    testLogging {
      events = setOf(PASSED, SKIPPED, FAILED)
    }
  }

  withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
      attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
  }

  withType<JavaExec> {
    args = listOf(
      "run",
      mainVerticleName,
      "--redeploy=$watchForChange",
      "--launcher-class=$launcherClassName",
      "--on-redeploy=$doOnChange"
    )
  }
}

