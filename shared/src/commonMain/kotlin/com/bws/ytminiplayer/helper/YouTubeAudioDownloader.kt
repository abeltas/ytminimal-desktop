package com.bws.ytminiplayer.helper

import com.bws.ytminiplayer.data.YtVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object YouTubeAudioDownloader {

    private val ytDlpPath: String by lazy { EmbeddedBinaries.getYtDlpPath() }
    private val ffmpegPath: String by lazy { EmbeddedBinaries.getFfmpegPath() }

    private val denoPath: String by lazy { EmbeddedBinaries.getDenoPath() }

    /**
     * Descarga solo el audio de YouTube y lo convierte a MP3
     */
    suspend fun downloadAudio(
        video : YtVideo,
        onProgress: (progress: Double, status: String) -> Unit,
        outputDir: String = getDefaultOutputDir(),
    ): Boolean = withContext(Dispatchers.IO) {

        if(!File(outputDir).isDirectory) throw Exception("El directorio no esiste ${outputDir}");

        val outputPath = File(outputDir).apply { mkdirs() }

        val outputTemplate = if (video.videoId.isNotEmpty()) {
            "\"${outputPath.absolutePath}\\${video.videoId}.%(ext)s\""
        } else {
            "\"${outputPath.absolutePath}\\%(title)s.%(ext)s\""
        }

        // --- Log ---

        val logsDir = AppPaths.dir("log")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        val logFile = File(logsDir, "log_$timestamp.txt")

        println(video.videoUrl)
        println(logsDir.absolutePath)
        println(logFile.absolutePath);

        //kotlinx.coroutines.delay(5000)
        //println(outputTemplate)

        val process = ProcessBuilder(
            ytDlpPath,
            "-v",
            "--js-runtimes", "deno:$denoPath",
            "--ffmpeg-location", ffmpegPath,
            "-x",
            "--audio-format", "mp3",         // ← Salida: MP3
            "--audio-quality", "0",          // ← 0=mejor, 5=buena, 9=peor
            "--embed-thumbnail",

            "-o", outputTemplate,
            "--newline",
            video.videoUrl
        ).apply {
            redirectErrorStream(true)
        }.start()

        //println(process.info())

        logFile.bufferedWriter().use { logWriter ->
            process.inputStream.bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    logWriter.write(line ?: "")
                    logWriter.newLine()
                    parseProgress(line ?: "", onProgress)
                }
            }
        }

        val exitCode = process.waitFor()

        //print(exitCode)
        //print(outputPath)

        if (exitCode == 0) {
            val mp3File = findMp3File(outputPath, video.videoId)
            if (mp3File != null) {
                // --- Forzar título/artista exactos desde el objeto video ---
                val taggedOk = embedMetadata(mp3File, video, logFile)
                if (!taggedOk) {
                    println("Advertencia: no se pudieron escribir los metadatos en ${mp3File.name}")
                }
                true
                //DownloadResult.Success(mp3File)
            } else {
                false
                //DownloadResult.Error("Archivo no encontrado")
            }
        } else {
            false
            //DownloadResult.Error("Error en la descarga")
        }
    }

    /**
     * Reescribe los tags ID3 (title/artist) del mp3 usando exactamente
     * los valores de YtVideo, sin depender de lo que YouTube haya etiquetado.
     * Usa ffmpeg -codec copy sobre un archivo temporal y luego reemplaza el original.
     */
    private fun embedMetadata(mp3File: File, video: YtVideo, logFile: File): Boolean {
        val tempFile = File(mp3File.parentFile, "${mp3File.nameWithoutExtension}.tmp.mp3")

        val process = ProcessBuilder(
            ffmpegPath,
            "-y",                          // sobrescribe si existe
            "-i", mp3File.absolutePath,
            "-metadata", "title=${video.title}",
            "-metadata", "artist=${video.artist}",
            "-codec", "copy",
            tempFile.absolutePath
        ).apply {
            redirectErrorStream(true)
        }.start()

        logFile.appendText("\n--- embedMetadata ---\n")
        process.inputStream.bufferedReader().use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                logFile.appendText((line ?: "") + "\n")
            }
        }

        val exitCode = process.waitFor()

        return if (exitCode == 0 && tempFile.exists()) {
            mp3File.delete()
            tempFile.renameTo(mp3File)
        } else {
            tempFile.delete()
            false
        }
    }

    // --- Métodos privados ---

    private fun getDefaultOutputDir(): String = AppPaths.dir("mp3/download").absolutePath

    private fun findMp3File(outputPath: File, fileName: String?): File? {
        return if (fileName != null) {
            File(outputPath, "$fileName.mp3").takeIf { it.exists() }
        } else {
            outputPath.listFiles { _, name -> name.endsWith(".mp3") }
                ?.maxByOrNull { it.lastModified() }
        }
    }

    private fun parseProgress(line: String, onProgress: (Double, String) -> Unit) {
        val regex = """(\d+\.?\d*)%\s+of\s+.*""".toRegex()
        val match = regex.find(line)
        if (match != null) {
            val percent = match.groupValues[1].toDoubleOrNull() ?: 0.0
            onProgress(percent / 100.0, line.trim())
        }
    }
}

sealed class DownloadResult {
    data class Success(val file: File) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}

data class VideoInfo(
    val title: String,
    val uploader: String,
    val duration: String,
    val thumbnailUrl: String
)