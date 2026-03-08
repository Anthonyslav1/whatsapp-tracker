import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.compose") version "1.6.0"
}

group = "com.whatsapptracker.pc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    
    // SQLite JDBC
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    
    // JNA
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}

compose.desktop {
    application {
        mainClass = "com.whatsapptracker.pc.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Ravdesk"
            packageVersion = "2.0.0"
        }
    }
}
