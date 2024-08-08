plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "j-agency"
include("mifare")
include("api")
include("common")
include("ccr-hfn")
include("reader")
