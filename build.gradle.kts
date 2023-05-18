plugins {
    idea
    java
    `maven-publish`
    id("org.springframework.boot") version "3.1.0-M2"
    id("io.spring.dependency-management") version "1.1.0"
    id("io.freefair.lombok") version "8.0.1"
}

group = "com.pauldaniv.promotion.yellowtaxi.client"
version = "0.0.3-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val awsDomainOwner: String = System.getenv("AWS_DOMAIN_OWNER_ID")
val codeArtifactRepository = "https://promotion-${awsDomainOwner}.d.codeartifact.us-east-2.amazonaws.com/maven/releases/"
val codeArtifactPassword: String? = System.getenv("CODEARTIFACT_AUTH_TOKEN")

repositories {
    maven {
        name = "CodeArtifact"
        url = uri(codeArtifactRepository)
        credentials {
            username = "aws"
            password = codeArtifactPassword
        }
    }
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.opencsv:opencsv:5.7.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Jar>("jar") {
    enabled = true
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
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar)
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
