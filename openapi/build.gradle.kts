plugins {
    base
    alias(libs.plugins.openapi.generator)
}

openApiGenerate {
    generatorName.set("openapi")
    inputSpec.set("$projectDir/src/main/openapi/index.yaml")
    outputDir.set(
        layout.buildDirectory
            .dir("generated/openapi")
            .get()
            .asFile.path,
    )

    configOptions.set(
        mapOf(
            "outputFile" to "openapi.json",
        ),
    )
}
