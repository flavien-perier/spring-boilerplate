import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
}

allprojects {
    group = "io.flavien"
    version = "1.0.0"
}

subprojects {
    val javaVersion = JavaVersion.VERSION_21

    tasks.withType<JavaCompile> {
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    if (project.name != "utils" && project.name != "frontend") {
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.jetbrains.kotlin.jvm")

        tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
            enabled = project.name == "api"
        }

        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
            enabled = project.name == "api"
        }

        the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
        }
    }
}
