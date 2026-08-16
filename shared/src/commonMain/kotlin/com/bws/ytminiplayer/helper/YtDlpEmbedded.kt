package com.bws.ytminiplayer.helper

import java.io.File

object EmbeddedBinaries {

    /**
     * Ruta base donde están los binarios.
     * Puedes cambiarla según tu estructura de carpetas.
     */

    private val cachedPaths = mutableMapOf<String, String>()

    fun getYtDlpPath(): String {
        return getBinaryPath("yt-dlp", "yt-dlp.exe")
    }

    fun getFfmpegPath(): String {
        return getBinaryPath("ffmpeg", "ffmpeg.exe")
    }

    fun getDenoPath(): String {
        return getBinaryPath("deno", "deno.exe")
    }

    private fun getBinaryPath(unixName: String, windowsName: String): String {

        val cached = cachedPaths[unixName]
        if (cached != null) return cached

        val osName = System.getProperty("os.name").lowercase()
        val binaryName = if (osName.contains("win")) windowsName else unixName

        // En vez de user.dir (rompe el arranque empaquetado), usamos la carpeta de
        // recursos que Compose Desktop expone automáticamente, tanto en `run` como
        // en `runDistributable`/instalador final.
        val resourcesDir = System.getProperty("compose.application.resources.dir")
            ?: File(System.getProperty("user.dir"), "src/resources").absolutePath

        val binaryDir = AppPaths.dir("bin")

        val binaryFile = File(binaryDir, binaryName)

        if (!binaryFile.exists()) {
            throw IllegalStateException(
                "Binario no encontrado: ${binaryFile.absolutePath}\n" +
                        "Verifica que exista en: $binaryDir"
            )
        }
        // Verificar que sea ejecutable
        if (!binaryFile.canExecute() && !osName.contains("win")) {
            binaryFile.setExecutable(true)
        }

        cachedPaths[unixName] = binaryFile.absolutePath
        return binaryFile.absolutePath
    }

}