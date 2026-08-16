import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val fxVersion = "21.0.5"
val fxOs = when {
    System.getProperty("os.name").startsWith("Windows") -> "win"
    System.getProperty("os.name").startsWith("Mac") -> "mac"
    else -> "linux"
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.components.resources)

    implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

    implementation("org.openjfx:javafx-base:$fxVersion:$fxOs")
    implementation("org.openjfx:javafx-graphics:$fxVersion:$fxOs")
    implementation("org.openjfx:javafx-media:$fxVersion:$fxOs")
    implementation("org.openjfx:javafx-swing:$fxVersion:$fxOs")

}

compose.desktop {
    application {
        mainClass = "com.bws.ytminiplayer.MainKt"
        //jvmArgs("-Duser.dir=F:\\Develop\\KotlinMultiPlatform\\KotlinYTMiniPlayer")

        //jvmArgs += "-Duser.dir=${rootProject.projectDir}"

        nativeDistributions {
            windows{
                includeAllModules = true
                iconFile.set(project.file("src/resources/app_icon.ico"))
            }
            //includeAllModules = true
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "YTMiniPlayer"
            packageVersion = "1.0.5"
            appResourcesRootDir.set(project.layout.projectDirectory.dir("src/resources"))

        }
    }
}