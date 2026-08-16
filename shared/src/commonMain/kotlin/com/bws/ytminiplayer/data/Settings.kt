package com.bws.ytminiplayer.data

import com.bws.ytminiplayer.audio.RepeatMode
import java.io.File
import java.util.Properties

/**
 * Preferencias persistentes del reproductor.
 *
 * Se guardan en un archivo .properties dentro del home del usuario:
 *   ~/.ytminiplayer/settings.properties
 *
 * Esta ubicacion persiste entre ejecuciones y sobrevive a reinstalaciones,
 * a diferencia de resources/ que se empaqueta y es de solo lectura.
 */
object Settings {

    private val file = File(
        System.getProperty("user.home"),
        ".ytminiplayer/settings.properties"
    )

    private const val KEY_VOLUME = "volume"
    private const val KEY_REPEAT = "repeatMode"

    // Valores por defecto (los que ya usabas)
    private const val DEFAULT_VOLUME = 0.7f
    private val DEFAULT_REPEAT = RepeatMode.OFF

    /** Carga las preferencias del disco (o los valores por defecto). */
    fun load(): Prefs {
        val props = Properties()
        if (file.exists()) {
            try {
                file.inputStream().use { props.load(it) }
            } catch (e: Exception) {
                // Si el archivo esta corrupto, usamos valores por defecto.
            }
        }

        val volume = props.getProperty(KEY_VOLUME)?.toFloatOrNull()
            ?.coerceIn(0f, 1f) ?: DEFAULT_VOLUME

        val repeat = props.getProperty(KEY_REPEAT)?.let {
            runCatching { RepeatMode.valueOf(it) }.getOrNull()
        } ?: DEFAULT_REPEAT

        return Prefs(volume = volume, repeatMode = repeat)
    }

    /** Guarda ambas preferencias en el disco. */
    fun save(volume: Float, repeatMode: RepeatMode) {
        try {
            file.parentFile?.mkdirs()
            val props = Properties()
            props.setProperty(KEY_VOLUME, volume.coerceIn(0f, 1f).toString())
            props.setProperty(KEY_REPEAT, repeatMode.name)
            file.outputStream().use { props.store(it, "YT Mini Player settings") }
        } catch (e: Exception) {
            // Si falla el guardado no rompemos la app; solo se pierde la persistencia.
        }
    }
}

/** Contenedor simple de las preferencias cargadas. */
data class Prefs(
    val volume: Float,
    val repeatMode: RepeatMode
)
