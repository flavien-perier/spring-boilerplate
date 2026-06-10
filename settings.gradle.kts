rootProject.name = "demo-parent"

include("libraries:library-common")
include("domain")
include("openapi")
include("api")
include("batch")
include("frontend")
include("libraries:library-vue3-components")
include("libraries:library-test")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
