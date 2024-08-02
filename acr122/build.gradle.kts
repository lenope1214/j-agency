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
    // ARC112 모듈에서 사용하는 라이브러리
    implementation("org.nfctools:nfctools-api:1.0.M8")
    implementation("org.nfctools:nfctools-core:1.0.M8")

    implementation(project(":common"))
    implementation(project(":reader"))
}
