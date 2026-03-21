import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.dependency.management) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.node) apply false
    alias(libs.plugins.graalvm.native) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.versions)
}

// Lint gradle
spotless {
    ratchetFrom("origin/main")
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeywords = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val stablePattern = "^[0-9,.v-]+(-r)?$".toRegex()
    return !(stableKeywords || stablePattern.matches(version))
}

// Check updates
tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.named("check") {
    dependsOn("dependencyUpdates")
}

allprojects {
    group = "io.flavien"
    version = "1.0.0"
}

val kotlinProjects = setOf("api", "batch", "domain", "utils", "openapi")
val frontendProjects = setOf("frontend", "component-library")

subprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = "25"
        targetCompatibility = "25"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_25)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // SBOM
    if (project.name in setOf("api", "batch", "domain")) {
        apply(plugin = "io.spring.dependency-management")

        the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
        }
    }

    // Lint Kotlin
    if (project.name in kotlinProjects) {
        apply(plugin = "com.diffplug.spotless")
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            ratchetFrom("origin/main")
            kotlin {
                target("**/*.kt")
                targetExclude("**/build/**", "**/dist/**")
                ktlint()
            }
            kotlinGradle {
                target("**/*.gradle.kts")
                targetExclude("**/build/**")
                ktlint()
            }
        }
        tasks.named("build") { dependsOn("spotlessApply") }
        tasks.named("check") { dependsOn("spotlessCheck") }
        tasks.named("spotlessCheck") { mustRunAfter("spotlessApply") }
    }

    // Lint Vue
    if (project.name in frontendProjects) {
        apply(plugin = "com.diffplug.spotless")
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            ratchetFrom("origin/main")
            format("web") {
                target(
                    "**/*.ts",
                    "**/*.tsx",
                    "**/*.js",
                    "**/*.jsx",
                    "**/*.vue",
                    "**/*.json",
                    "**/*.css",
                    "**/*.scss",
                    "**/*.md",
                )
                targetExclude(
                    "**/build/**",
                    "**/.gradle/**",
                    "**/dist/**",
                    "**/node_modules/**",
                    "**/generated/**",
                    "**/storybook-static/**",
                    "**/package-lock.json",
                )
                prettier()
            }
        }
        tasks.named("build") { dependsOn("spotlessApply") }
        tasks.named("check") { dependsOn("spotlessCheck") }
        tasks.named("spotlessCheck") { mustRunAfter("spotlessApply") }
    }
}
