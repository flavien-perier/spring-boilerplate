plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    enabled = true
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    enabled = false
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

dependencies {
    api(project(":libraries:library-common"))

    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.liquibase)

    implementation(libs.spring.security.crypto)
    implementation(libs.bouncycastle)

    implementation(libs.kotlin.reflect)

    runtimeOnly(libs.postgresql)
    implementation(libs.jedis)
    implementation(libs.jackson.dataformat.yaml)
    implementation(libs.jackson.module.kotlin)

    implementation(libs.resilience4j.spring.boot)
    runtimeOnly(libs.logstash.logback.encoder)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.subethasmtp)
    testImplementation(project(":libraries:library-test"))
}
