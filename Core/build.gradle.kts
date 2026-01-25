plugins {
    id("java")
}

group = "dev.watercooler.coolcoin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.bouncycastle:bcprov-jdk18on:1.83")
    implementation("io.netty:netty-all:5.0.0.Alpha2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.23")
    implementation("org.projectlombok:lombok:1.18.42")
}

tasks.test {
    useJUnitPlatform()
}