plugins {
    kotlin("multiplatform") version "1.4.32"
    //kotlin("multiplatform") version "1.5.0"
    //kotlin("multiplatform") version "1.5.20-dev-5655"
    //kotlin("multiplatform") version "1.5.255-SNAPSHOT"
}

group = "me.soywi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }
}
