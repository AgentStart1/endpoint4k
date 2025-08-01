plugins {
    kotlin("jvm") version "2.1.21"
    `maven-publish`
    id("common-publish")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}