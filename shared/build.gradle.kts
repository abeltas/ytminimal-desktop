plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.0"
}

kotlin {
    jvm()
    
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Ktor client para HTTP
            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation("io.ktor:ktor-client-cio:2.3.7") // Motor CIO

            implementation("net.jthink:jaudiotagger:3.0.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

            implementation("org.slf4j:slf4j-simple:2.0.9")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}