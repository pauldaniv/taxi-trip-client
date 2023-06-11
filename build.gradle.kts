plugins {
  java
  `maven-publish`
  id("org.springframework.boot") version "3.1.0"
  id("io.spring.dependency-management") version "1.1.0"
  id("io.freefair.lombok") version "8.0.1"
}

group = "com.pauldaniv.promotion.yellowtaxiclient"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String? = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
  mavenCentral()
  mavenLocal()
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
  implementation("com.pauldaniv.promotion.yellowtaxi.facade:api:0.0.1-SNAPSHOT")
  implementation("com.pauldaniv.promotion.yellowtaxi:api:0.0.6-SNAPSHOT")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

  implementation("org.apache.commons:commons-csv:1.10.0")

  implementation("org.springframework.boot:spring-boot-starter")
//  implementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
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
    create<MavenPublication>("maven") {
      versionMapping {
        usage("java-api") {
          fromResolutionOf("runtimeClasspath")
        }
        usage("java-runtime") {
          fromResolutionResult()
        }
      }
    }
  }
}

tasks.withType<JavaCompile> {
  sourceCompatibility = "17"
  targetCompatibility = "17"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
