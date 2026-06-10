plugins {
    `java-library`
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
    nodeProjectDir.set(file("$projectDir/src/main/typescript"))
}

val yarnInstall =
    tasks.named<com.github.gradle.node.yarn.task.YarnInstallTask>("yarn") {
    }

val yarnBuild =
    tasks.register<com.github.gradle.node.yarn.task.YarnTask>("yarnBuild") {
        dependsOn(yarnInstall)
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
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(yarnBuild)
    from("src/main/typescript/dist") {
        into("static")
    }
}
