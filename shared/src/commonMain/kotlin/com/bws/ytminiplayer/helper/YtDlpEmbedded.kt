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

        val workingDir = File(System.getProperty("user.dir"))
        var binaryDir = File(workingDir.absolutePath, "src/resources/bin")

        // Construir ruta completa
        val binaryFile = File(binaryDir, binaryName)

        if (!binaryFile.exists()) {
            throw IllegalStateException(
                "Binario no encontrado: ${binaryFile.absolutePath}\n" +
                        "Verifica que exista en: $workingDir"
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