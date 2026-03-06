plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    js {
        browser()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    sourceSets {
        commonMain {
            kotlin.srcDirs("src/main/kotlin")
        }
    }
}
