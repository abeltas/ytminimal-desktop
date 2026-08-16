package com.bws.ytminiplayer.audio

import java.io.File

/**
 * Localiza la carpeta de musica del proyecto.
 *
 * Prioridad:
 *   1. Si existe la subcarpeta 'download' DENTRO de mp3 y contiene algun .mp3,
 *      se usa esa (ignorando los mp3 del path raiz).
 *   2. Si no, se usa la carpeta 'mp3' base.
 *
 * Durante el desarrollo (ejecucion con Gradle) el working dir puede ser la raiz
 * del proyecto o el modulo, por eso probamos varias rutas candidatas.
 */
object MusicFolder {

    fun resolve(): File {
        val base = resolveBase()

        // Subcarpeta download dentro de la base.
        val download = File(base, "download")
        if (download.exists() && hasMp3(download)) {
            return download
        }

        // Si no hay mp3 en download (o no existe), usamos la base.
        return base
    }

    /** Localiza la carpeta 'mp3' base, creandola si no existe. */
    private fun resolveBase(): File {
        val workingDir = File(System.getProperty("user.dir"))
        val candidates = listOf(
            File(workingDir.absolutePath, "src/resources/mp3"),
            //File(workingDir.absolutePath ?: workingDir, "src/resources/mp3"),
            //File(workingDir, "mp3"),
        )
        val found = candidates.firstOrNull { it.exists() }
        if (found != null) return found

        // No existe todavia: la creamos en la primera candidata.
        ///return File(workingDir.absolutePath, "src/resources/mp3").apply { mkdirs() }
        print(workingDir.absolutePath)
        throw Exception("No existe la carpeta ${candidates.joinToString { it.path }}");
    }

    /** True si la carpeta contiene al menos un .mp3. */
    private fun hasMp3(folder: File): Boolean =
        folder.listFiles { f -> f.isFile && f.extension.equals("mp3", ignoreCase = true) }
            ?.isNotEmpty() == true

    // En MusicFolder.kt (o donde prefieras)
    fun existsMp3(videoId: String): Boolean {
        val folder = resolve()   // tu carpeta de mp3 (o la subcarpeta download)
        return File(folder, "download/$videoId.mp3").exists()
    }
}