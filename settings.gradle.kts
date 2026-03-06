rootProject.name = "demo-parent"

include("utils")
include("domain")
include("api")
include("frontend")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
