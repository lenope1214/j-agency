import org.springframework.boot.gradle.tasks.bundling.BootJar

// plain jar 생성 X
val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = true
jar.enabled = false

// jar filename 변경
bootJar.archiveFileName.set("jagency-batch.jar")

// 입력 순번 오름차순으로 수정
dependencies {
    implementation(project(":common"))
    implementation(project(":file-csv"))
}
