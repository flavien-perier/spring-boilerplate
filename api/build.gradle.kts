import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.cyclonedx)
}

tasks.cyclonedxDirectBom {
    schemaVersion = org.cyclonedx.Version.VERSION_16
    includeMetadataResolution = true
    xmlOutput.unsetConvention()
}

if (project.hasProperty("native")) {
    apply(plugin = "org.graalvm.buildtools.native")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":frontend"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.tracing.bridge.brave)
    implementation(libs.spring.boot.micrometer.tracing.brave)
    runtimeOnly(libs.zipkin.reporter.brave)
    developmentOnly(libs.spring.boot.dockercompose)

    implementation(libs.jackson.databind.nullable)
    implementation(libs.springdoc.openapi)
    implementation(libs.swagger.parser)

    implementation(libs.mapstruct)
    kapt(libs.mapstruct.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webflux)
    testImplementation(libs.mockk)
    testImplementation(libs.assertj)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.subethasmtp)
    testImplementation(project(":libraries:library-test"))
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("${rootProject.projectDir}/openapi/build/bundled/openapi.json")
    outputDir.set(
        layout.buildDirectory
            .dir("generated/openapi")
            .get()
            .asFile.path,
    )
    apiPackage.set("io.flavien.demo.api.generated.api")
    modelPackage.set("io.flavien.demo.api.generated.dto")

    additionalProperties.set(
        mapOf(
            "developerEmail" to "perier@flavien.io",
            "developerName" to "Flavien PERIER",
            "developerOrganization" to "flavien.io",
            "developerOrganizationUrl" to "https://www.flavien.io/",
        ),
    )

    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "useTags" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useSpringController" to "true",
            "serializableModel" to "true",
            "useEnumCaseInsensitive" to "true",
        ),
    )
}

val copyOpenApiYaml =
    tasks.register<Copy>("copyOpenApiYaml") {
        dependsOn(":openapi:bundleSpec")
        from("${rootProject.projectDir}/openapi/build/bundled/openapi.json")
        into(layout.buildDirectory.dir("resources/main"))
    }

sourceSets {
    main {
        kotlin {
            srcDir(layout.buildDirectory.dir("generated/openapi/src/main/kotlin"))
        }
    }
}

tasks.named("openApiGenerate") {
    dependsOn(":openapi:bundleSpec")
}

tasks.withType<KotlinCompile> {
    dependsOn("openApiGenerate")
}

tasks.named("processResources") {
    dependsOn(copyOpenApiYaml)
}

springBoot {
    mainClass.set("io.flavien.demo.api.ApplicationKt")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args("--spring.profiles.active=dev")
    workingDir = rootProject.projectDir
}

if (project.hasProperty("native")) {
    extensions.configure<org.graalvm.buildtools.gradle.dsl.GraalVMExtension> {
        binaries {
            named("main") {
                imageName.set("demo")
                buildArgs.add("--static")
                buildArgs.add("--libc=musl")
            }
        }
    }
}
