package com.bws.ytminiplayer.data

import com.bws.ytminiplayer.config.YouTubeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.parser.ParserDelegator

/** Resultado ya listo para la UI. */
data class YtVideo(
    val videoId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String,
    val videoUrl: String
)

/**
 * Cliente para la YouTube Data API v3 usando HttpURLConnection (sin Ktor).
 * Parsea la respuesta con kotlinx.serialization.
 */
object YouTubeApi {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun search(query: String): List<YtVideo> = withContext(Dispatchers.IO) {
        val term = query.trim()

        // 1. Buscar en cache local
        val cached = SearchCache.get(term)
        val raw: String? = if (cached != null) {
            println("SearchCache: HIT para \"$term\" (sin llamar a la API)")
            cached
        } else {
            println("SearchCache: MISS para \"$term\" -> llamando a la API")
            val fresh = fetch(YouTubeConfig.buildSearchUrl(term))
            if (fresh != null) {
                SearchCache.put(term, fresh)   // guarda el JSON crudo
            }
            fresh
        }

        if (raw == null) return@withContext emptyList()

        // 2. Parsear (igual venga de cache o de la red)
        try {
            val parsed = json.decodeFromString<YtSearchResponse>(raw)
            parsed.items.mapNotNull { item ->
                val vid = item.id?.videoId ?: return@mapNotNull null
                val sn = item.snippet
                val thumb = sn?.thumbnails?.medium?.url
                    ?: sn?.thumbnails?.default?.url
                    ?: ""
                YtVideo(
                    videoId = vid,
                    title = decodeHtmlEntities(sn?.title.orEmpty()).ifBlank { "Sin titulo" },
                    artist = decodeHtmlEntities(sn?.channelTitle.orEmpty()).ifBlank { "Desconocido" },
                    thumbnailUrl = thumb,
                    videoUrl = "https://www.youtube.com/watch?v=$vid"
                )
            }
        } catch (e: Exception) {
            println("YouTubeApi: error al parsear -> ${e.message}")
            emptyList()
        }
    }

    /** Decodifica TODAS las entidades HTML (nombradas y numericas) usando el parser del JDK. */
    private fun decodeHtmlEntities(text: String): String {
        if (text.isEmpty()) return text
        val result = StringBuilder()
        return try {
            val callback = object : HTMLEditorKit.ParserCallback() {
                override fun handleText(data: CharArray, pos: Int) {
                    result.append(data)
                }
            }
            ParserDelegator().parse(StringReader(text), callback, true)
            result.toString()
        } catch (e: Exception) {
            text
        }
    }

    private fun fetch(urlStr: String): String? {
        println("YouTubeApi: url -> ${urlStr}")
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlStr)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Accept", "application/json")
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }
            if (code in 200..299) body else {
                //println("YouTubeApi: HTTP $code -> $body")
                null
            }
        } catch (e: Exception) {
            println("YouTubeApi: error -> ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }
}
