package com.bws.ytminiplayer.helper

import java.io.File

object AppPaths {

    /** Carpeta base de recursos, válida en `run` y en `runDistributable`. */
    fun baseDir(): File {
        // Compilado: Compose expone la ruta real
        System.getProperty("compose.application.resources.dir")?.let { return File(it) }
        // Desarrollo: árbol de fuentes
        return File(System.getProperty("user.dir"), "desktopApp/src/resources")
    }

    /** Devuelve una subcarpeta y la crea si no existe. */
    fun dir(sub: String): File = File(baseDir(), sub).apply { mkdirs() }
}