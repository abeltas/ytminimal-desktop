package com.bws.ytminiplayer.data

import com.bws.ytminiplayer.helper.AppPaths
import java.io.File
import java.util.Base64

/**
 * Cache de busquedas en disco: resources/cache/search/<queryBase64>.json
 * Guarda el JSON CRUDO de la respuesta de YouTube. Permite reescribir.
 */
object SearchCache {

    /** Carpeta resources/cache/search, resuelta como las demas (varias candidatas). */
    private fun resolveFolder(): File {
        val workingDir = File(System.getProperty("user.dir"))
        val candidates = listOf(
            File(workingDir, "src/resources/cache/search"),
            //File(workingDir.parentFile ?: workingDir, "resources/cache/search"),
            //File(workingDir, "cache/search"),
        )
        val found = candidates.firstOrNull { it.exists() }
        if (found != null) return found
        throw Exception("No existe la carpeta ${candidates.joinToString { it.path }}");
        //return File(workingDir, "resources/cache/search").apply { mkdirs() }
    }

    /** Normaliza la query (minusculas + trim) y la codifica en Base64 para el nombre. */
    private fun fileFor(query: String): File {
        val normalized = query.trim().lowercase()
        val encoded = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(normalized.toByteArray(Charsets.UTF_8))
        val folder = AppPaths.dir("cache/search")
        return File(folder, "$encoded.json")
    }

    /** Devuelve el JSON crudo cacheado para esa query, o null si no existe. */
    fun get(query: String): String? {
        val file = fileFor(query)
        return if (file.exists()) {
            try { file.readText(Charsets.UTF_8) } catch (e: Exception) { null }
        } else null
    }

    /** Guarda (o reescribe) el JSON crudo para esa query. */
    fun put(query: String, rawJson: String) {
        try {
            val file = fileFor(query)
            file.parentFile?.mkdirs()
            file.writeText(rawJson, Charsets.UTF_8)
        } catch (e: Exception) {
            println("SearchCache: no se pudo guardar -> ${e.message}")
        }
    }
}