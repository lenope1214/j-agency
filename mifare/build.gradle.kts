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
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    implementation(project(":common"))
    implementation(project(":reader"))
}
