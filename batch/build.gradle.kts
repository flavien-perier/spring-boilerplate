plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.spring.boot.starter.batch)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.boot.starter.mail)
    testImplementation(libs.archunit.junit5)
}

tasks.withType<Test> {
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
}

springBoot {
    mainClass.set("io.flavien.demo.batch.ApplicationKt")
}
