plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    enabled = true
}

dependencies {
    api(project(":utils"))

    api(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.liquibase)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)

    runtimeOnly(libs.postgresql)
    implementation(libs.jedis)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.subethasmtp)
}
