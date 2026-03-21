plugins {
    `java-library`
    alias(libs.plugins.node)
}

node {
    version.set("24.14.0")
    download.set(true)
    workDir.set(layout.buildDirectory.dir("nodejs"))
    npmWorkDir.set(layout.buildDirectory.dir("npm"))
    nodeProjectDir.set(file("${projectDir}/src/main/typescript"))
}

val npmInstall = tasks.named<com.github.gradle.node.npm.task.NpmInstallTask>("npmInstall")

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
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(npmBuild)
    from("src/main/typescript/dist") {
        into("static")
    }
}
