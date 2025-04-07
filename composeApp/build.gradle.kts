import org.gradle.kotlin.dsl.internal.sharedruntime.codegen.fileHeader
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinCocoapods) version "2.1.20"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    version = "2.1.20"
    cocoapods {
        summary = "..."
        homepage = "..."

        framework {
            baseName = "shared"
            // This is important to make Swift accessible to Kotlin
            export("dev.icerock:biometry:0.1.0")
        }
    }

    cocoapods {
        summary = "Shared Code"
        homepage = "https://example.com"

        framework {
            baseName = "shared"
            // Set export ObjC header for Swift
            export = true
            embedBitcode(org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode.DISABLE)
        }

        // You may need to add this
        pod("BiometricModule", path = File("../iosApp"))
    }


    sourceSets {
        // Common code for all platforms
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Core coroutines for all platforms
                implementation(libs.kotlinx.coroutines)
            }
        }

        // Android-specific code
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // Android-specific coroutines
                implementation(libs.kotlinx.coroutines)

                // Biometric dependencies - Android only
                implementation(libs.androidx.biometric)
                implementation(libs.androidx.biometric.ktx)

                // Android lifecycle
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)

                // Android fragment
                implementation(libs.androidx.fragment.ktx)
            }
        }



        // Remove all specific iOS architecture dependencies that include Android libraries
        // Keep these empty or with minimal iOS-specific dependencies
        val iosArm64Main by getting
        val iosX64Main by getting
        val iosSimulatorArm64Main by getting
    }
}

// Make sure iOS configurations exclude Android dependencies
configurations.all {
    if (name.contains("Ios") || name.contains("ios")) {
        exclude(group = "androidx")
        exclude(module = "kotlinx-coroutines-android")
    }
}

android {
    namespace = "org.example.myapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.myapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Android-specific dependencies
dependencies {
    implementation(libs.firebase.database.ktx)
    debugImplementation(compose.uiTooling)
}

// Add specific tasks to help with iOS build issues
tasks.register("cleanIosTargets") {
    doLast {
        file("build/ios").deleteRecursively()
    }
}

tasks.named("clean") {
    dependsOn("cleanIosTargets")
}

// Add this to force Kotlin to compile with proper target compatibility
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}