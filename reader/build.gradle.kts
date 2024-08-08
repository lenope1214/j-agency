import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

plugins {
    java
    `java-test-fixtures`
}

dependencies {
    implementation(project(":common"))

    // mifare 사용시 사용됨
    api("org.nfctools:nfctools-api:1.0.M8")
    api("org.nfctools:nfctools-core:1.0.M8")
}
