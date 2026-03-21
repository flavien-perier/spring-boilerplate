rootProject.name = "demo-parent"

include("utils")
include("domain")
include("openapi")
include("api")
include("frontend")
include("component-library")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
