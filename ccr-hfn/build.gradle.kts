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
    implementation("net.java.dev.jna:jna:5.8.0")
    implementation("com.google.guava:guava:18.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("commons-codec:commons-codec:1.13")
    implementation("commons-io:commons-io:2.11.0")

    implementation(project(":common"))

    // CCR 32  rfid 전용 파일 root/CCR_32.dll
    implementation(fileTree(mapOf("dir" to "", "include" to listOf("CCR_32.dll"))))
}
