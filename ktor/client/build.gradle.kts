plugins {
    kotlin("jvm") version "2.1.21"
    id("common-publish")
}

dependencies {
    implementation(project(":common"))
    implementation("io.ktor:ktor-client-core:3.1.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}
