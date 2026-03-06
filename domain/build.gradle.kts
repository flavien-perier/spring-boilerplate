plugins {
    `java-library`
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    enabled = true
}

dependencies {
    api(project(":utils"))

    api(libs.spring.boot.starter.data.jpa)
    api(libs.spring.boot.starter.data.redis)
    api(libs.spring.boot.starter.mail)
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.thymeleaf)
    api(libs.spring.boot.starter.liquibase)

    api(libs.kotlin.reflect)
    api(libs.kotlin.stdlib)

    runtimeOnly(libs.postgresql)
    api(libs.jedis)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.subethasmtp)
}
