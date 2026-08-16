package com.bws.ytminiplayer.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage
import java.awt.image.BufferedImage
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

/**
 * Cache de miniaturas en disco: resources/img/thumb/<videoId>.jpg
 *
 * - Si el archivo ya existe, se carga de disco (sin red).
 * - Si no, se descarga de la URL, se recorta al cuadrado, se GUARDA recortada
 *   y se devuelve. Asi la proxima vez sale del disco y no se vuelve a recortar.
 */
object ThumbCache {

    private const val SIZE = 120   // lado del cuadrado guardado (px)

    /** Carpeta resources/img/thumb, resuelta como MusicFolder (varias candidatas). */
    private fun resolveFolder(): File {
        val workingDir = File(System.getProperty("user.dir"))
        val candidates = listOf(
            File(workingDir, "src/resources/img/thumb"),
            //File(workingDir.parentFile ?: workingDir, "resources/img/thumb"),
            //File(workingDir, "img/thumb"),
        )
        val found = candidates.firstOrNull { it.exists() }
        if (found != null) return found
        //return File(workingDir, "resources/img/thumb").apply { mkdirs() }
        throw Exception("No existe la carpeta ${candidates.joinToString { it.path }}");
    }

    /**
     * Devuelve la miniatura (cuadrada) para un video.
     * Busca en disco por videoId; si no esta, descarga+recorta+guarda.
     */
    suspend fun getThumbnail(videoId: String, url: String): ImageBitmap? =
        withContext(Dispatchers.IO) {
            if (videoId.isBlank()) return@withContext null
            val folder = resolveFolder()
            val file = File(folder, "$videoId.jpg")

            // 1. Existe en disco -> cargar directo
            if (file.exists()) {
                return@withContext try {
                    SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }

            // 2. No existe -> descargar, recortar al cuadrado, guardar
            if (url.isBlank()) return@withContext null
            try {
                val original = downloadImage(url) ?: return@withContext null
                val squared = cropToSquare(original, SIZE)
                folder.mkdirs()
                ImageIO.write(squared, "jpg", file)   // guarda ya recortada
                SkiaImage.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
            } catch (e: Exception) {
                println("ThumbCache: error con $videoId -> ${e.message}")
                null
            }
        }

    /** Descarga la imagen de la URL como BufferedImage. */
    private fun downloadImage(urlStr: String): BufferedImage? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlStr)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            connection.inputStream.use { ImageIO.read(it) }
        } catch (e: Exception) {
            println("ThumbCache: descarga fallida -> ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }

    /** Recorta al centro un cuadrado y lo escala al tamaño dado. */
    private fun cropToSquare(src: BufferedImage, size: Int): BufferedImage {
        val side = minOf(src.width, src.height)
        val x = (src.width - side) / 2
        val y = (src.height - side) / 2
        val cropped = src.getSubimage(x, y, side, side)

        val out = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        val g = out.createGraphics()
        g.drawImage(cropped.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null)
        g.dispose()
        return out
    }
}
