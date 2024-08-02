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
//    testFixturesImplementation("io.github.serpro69:kotlin-faker:1.15.0") // kotlin-faker
}
