plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "j-agency"
include("ccr-hfn")
include("common")
include("file")
include("file-csv")
include("mifare")
include("reader")

// application module
include("daegyung")
include("daedong")
