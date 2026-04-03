plugins {
    `java-library`
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.node)
}

openApiGenerate {
    generatorName.set("typescript-axios")
    inputSpec.set("${rootProject.projectDir}/openapi/src/main/openapi/index.yaml")
    outputDir.set("${projectDir}/src/main/typescript/generated/api-source")

    configOptions.set(mapOf(
        "npmName" to "api-generated",
        "withNodeImports" to "false",
        "enumPropertyNaming" to "UPPERCASE",
    ))
}

tasks.named("openApiGenerate") {
    dependsOn(":openapi:openApiGenerate")
}

node {
    version.set(libs.versions.node.version.get())
    download.set(true)
    workDir.set(layout.buildDirectory.dir("nodejs"))
    npmWorkDir.set(layout.buildDirectory.dir("npm"))
    nodeProjectDir.set(file("${projectDir}/src/main/typescript"))
}

val npmInstallApiSource = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmInstallApiSource") {
    dependsOn("openApiGenerate")
    workingDir.set(file("${projectDir}/src/main/typescript/generated/api-source"))
    args.set(listOf("install", "--ignore-scripts"))
}

val apiSourceDistDir = layout.projectDirectory.dir("src/main/typescript/generated/api-source/dist")
val apiDistDir = layout.projectDirectory.dir("src/main/typescript/generated/api")

val patchApiSource = tasks.register("patchApiSource") {
    dependsOn("openApiGenerate")
    val tsConfigFile = file("${projectDir}/src/main/typescript/generated/api-source/tsconfig.json")
    val commonFile = file("${projectDir}/src/main/typescript/generated/api-source/common.ts")
    val apiFile = file("${projectDir}/src/main/typescript/generated/api-source/api.ts")
    val packageJsonFileForInputs = file("${projectDir}/src/main/typescript/generated/api-source/package.json")
    inputs.files(tsConfigFile, commonFile, apiFile, packageJsonFileForInputs)
    outputs.files(tsConfigFile, commonFile, apiFile, packageJsonFileForInputs)
    doLast {
        val tsConfig = tsConfigFile.readText()
        val typeRootsPattern = Regex(""""typeRoots":\s*\[[^\]]*\]""", RegexOption.DOT_MATCHES_ALL)
        tsConfigFile.writeText(
            tsConfig
                .replace(""""module": "commonjs"""", """"module": "ES6"""")
                .replace(""""target": "ES5"""", """"target": "ES6"""")
                .let { typeRootsPattern.replace(it, """"moduleResolution": "node",${System.lineSeparator()}    "types": []""") }
        )
        for (sourceFile in listOf(commonFile, apiFile)) {
            val content = sourceFile.readText()
            sourceFile.writeText(
                content.replace("import { URL, URLSearchParams } from 'url';\n", "")
                       .replace("import { URL, URLSearchParams } from 'url';\r\n", "")
            )
        }

        val packageJsonFile = file("${projectDir}/src/main/typescript/generated/api-source/package.json")
        val packageJson = packageJsonFile.readText()
        packageJsonFile.writeText(
            packageJson.replace(""""@types/node": "12.11.5 - 12.20.42",${System.lineSeparator()}    """, "")
                       .replace(""""@types/node": "12.11.5 - 12.20.42",\n    """, "")
        )
    }
}

val npmBuildApiSource = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuildApiSource") {
    dependsOn(npmInstallApiSource)
    dependsOn(patchApiSource)
    workingDir.set(file("${projectDir}/src/main/typescript/generated/api-source"))
    args.set(listOf("run", "build"))
    outputs.dir(apiSourceDistDir)
}

val copyApiDist = tasks.register<Copy>("copyApiDist") {
    dependsOn(npmBuildApiSource)
    from(npmBuildApiSource.map { it.outputs.files })
    into(apiDistDir)
}

val copyUtilsJs = tasks.register<Sync>("copyUtilsJs") {
    dependsOn(":utils:jsBrowserProductionLibraryDistribution")
    from("${rootProject.projectDir}/utils/build/dist/js/productionLibrary")
    into("${projectDir}/src/main/typescript/generated/utils")
}

val copyComponentLibrary = tasks.register<Sync>("copyComponentLibrary") {
    dependsOn(":component-library:npmBuild")
    from("${rootProject.projectDir}/component-library/src/main/typescript/dist")
    into("${projectDir}/src/main/typescript/generated/component-library")
}

val npmInstall = tasks.named<com.github.gradle.node.npm.task.NpmInstallTask>("npmInstall") {
    dependsOn(copyApiDist)
    dependsOn(copyUtilsJs)
    dependsOn(copyComponentLibrary)
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

tasks.named("clean") {
    delete("src/main/typescript/dist")
    delete("src/main/typescript/generated")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(npmBuild)
    from("src/main/typescript/dist") {
        into("static")
    }
}
