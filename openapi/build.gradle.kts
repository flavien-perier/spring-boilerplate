plugins {
    base
    alias(libs.plugins.node)
}

node {
    version.set(
        libs.versions.node.version
            .get(),
    )
    yarnVersion.set(
        libs.versions.yarn.version
            .get(),
    )
    download.set(true)
    workDir.set(layout.buildDirectory.dir("nodejs"))
    yarnWorkDir.set(layout.buildDirectory.dir("yarn"))
    nodeProjectDir.set(file("$projectDir/src/main/openapi"))
}

val bundledSpecFile =
    layout.buildDirectory
        .file("bundled/openapi.json")
        .get()
        .asFile

val yarnInstall = tasks.named<com.github.gradle.node.yarn.task.YarnInstallTask>("yarn")

val bundleSpec =
    tasks.register<com.github.gradle.node.yarn.task.YarnTask>("bundleSpec") {
        dependsOn(yarnInstall)

        val inputDir = file("$projectDir/src/main/openapi")
        inputs.files(fileTree(inputDir) { exclude("node_modules/**") })
        outputs.file(bundledSpecFile)

        args.set(listOf("run", "bundle"))

        doFirst {
            bundledSpecFile.parentFile.mkdirs()
        }
    }

tasks.named("assemble") {
    dependsOn(bundleSpec)
}
