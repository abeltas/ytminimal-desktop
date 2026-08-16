package com.bws.ytminiplayer.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado de reproducción compartido.
 * Vive fuera de los composables para que sobreviva a recomposiciones
 * y pueda conectarse luego al reproductor real de audio.
 */
class PlaybackState {
    // Estado observable: la UI se redibuja cuando cambia.
    var isPlaying by mutableStateOf(false)
        private set   // solo se modifica a través de los métodos de abajo

    fun play() {
        isPlaying = true
        // TODO: aquí llamarás a audioController.play()
        println("playing")
    }

    fun stop() {
        isPlaying = false
        // TODO: aquí llamarás a audioController.stop()
        println("stop")
    }

    fun toggle() {
        if (isPlaying) stop() else play()
    }

    /** Setter directo por si quieres forzar el valor desde el código original. */
    fun set(value: Boolean) {
        isPlaying = value
    }
}