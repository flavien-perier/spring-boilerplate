plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    js {
        browser {
            testTask {
                enabled = false
            }
        }
        generateTypeScriptDefinitions()
        binaries.library()
    }

    sourceSets {
        commonMain {
            kotlin.srcDirs("src/commonMain/kotlin")
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
