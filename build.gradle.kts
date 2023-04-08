plugins {
  java
  `maven-publish`
  id("org.springframework.boot") version "3.0.5"
  id("io.spring.dependency-management") version "1.1.0"
}

group = "com.pauldaniv.promotion.yellowtaxiclient"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
  mavenCentral()
  maven {
    name = "CodeArtifact"
    url = uri(codeArtifactRepository)
    credentials {
      username = "aws"
      password = codeArtifactPassword
    }
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-rest")
  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

val sourcesJar by tasks.creating(Jar::class) {
  archiveClassifier.set("sources")
  from(sourceSets["main"].allSource)
}

publishing {
  repositories {
    maven {
      name = "CodeArtifactPackages"
      url = uri(codeArtifactRepository)
      credentials {
        username = "aws"
        password = codeArtifactPassword
      }
    }
  }

  publications {
    register<MavenPublication>("publishDependencies") {
      from(components["java"])
      artifact(sourcesJar)
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
