plugins {
    `java-library`
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.node)
}

openApiGenerate {
    generatorName.set("typescript-axios")
    inputSpec.set("${projectDir}/../api/src/main/resources/openapi.yaml")
    outputDir.set("${projectDir}/src/main/typescript/generated/api")

    configOptions.set(mapOf(
        "npmName" to "api-generated",
        "withNodeImports" to "false",
        "enumPropertyNaming" to "UPPERCASE",
    ))
}

node {
    version.set("24.14.0")
    download.set(true)
    workDir.set(layout.buildDirectory.dir("nodejs"))
    npmWorkDir.set(layout.buildDirectory.dir("npm"))
    nodeProjectDir.set(file("${projectDir}/src/main/typescript"))
}

val copyUtilsJs = tasks.register<Sync>("copyUtilsJs") {
    dependsOn(":utils:jsBrowserProductionLibraryDistribution")
    from("${rootProject.projectDir}/utils/build/dist/js/productionLibrary")
    into("${projectDir}/src/main/typescript/generated/utils")
}

val npmInstall = tasks.named<com.github.gradle.node.npm.task.NpmInstallTask>("npmInstall") {
    dependsOn("openApiGenerate")
    dependsOn(copyUtilsJs)
}

val npmBuild = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuild") {
    dependsOn(npmInstall)
    args.set(listOf("run", "build"))
}

sourceSets {
    main {
        resources {
            srcDir("src/main/typescript/dist")
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(npmBuild)
    from("src/main/typescript/dist") {
        into("static")
    }
}
