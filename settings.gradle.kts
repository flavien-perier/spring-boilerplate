rootProject.name = "demo-parent"

include("utils")
include("domain")
include("api")
include("frontend")
include("component-library")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
