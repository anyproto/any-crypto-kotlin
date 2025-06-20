plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
}

group = "com.github.anyproto"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("com.github.komputing:KBase58:0.4")
    
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(18)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}