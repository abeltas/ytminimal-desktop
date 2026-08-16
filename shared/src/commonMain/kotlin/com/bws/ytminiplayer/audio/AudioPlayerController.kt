package com.bws.ytminiplayer.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.bws.ytminiplayer.data.Settings
import javafx.application.Platform
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jetbrains.skia.Image as SkiaImage
import java.io.File

/** Modos de repeticion. */
enum class RepeatMode { OFF, ALL, ONE }

/** Informacion de una pista para mostrar en la biblioteca. */
data class TrackInfo(
    val title: String,
    val artist: String,
    val durationSeconds: Int
)

/**
 * Controlador de reproduccion basado en JavaFX Media.
 */
class AudioPlayerController {

    // ---- Estado observable que lee la UI ----
    var isPlaying by mutableStateOf(false)
        private set

    var progress by mutableStateOf(0f)
        private set

    var currentSeconds by mutableStateOf(0)
        private set

    var durationSeconds by mutableStateOf(0)
        private set

    var title by mutableStateOf("")
        private set

    var artist by mutableStateOf("")
        private set

    var cover by mutableStateOf<ImageBitmap?>(null)
        private set

    var volume by mutableStateOf(0.7f)
        private set

    var repeatMode by mutableStateOf(RepeatMode.OFF)
        private set

    // ---- Playlist / navegacion ----
    private var playlist: List<File> = emptyList()

    /** Metadatos de todas las pistas, para la biblioteca. Observable. */
    var tracks by mutableStateOf<List<TrackInfo>>(emptyList())
        private set

    var currentIndex by mutableStateOf(-1)
        private set

    val trackCount: Int get() = playlist.size
    val hasPrevious: Boolean get() = currentIndex > 0
    val hasNext: Boolean get() = currentIndex in 0 until (playlist.size - 1)

    private var player: MediaPlayer? = null
    private var seeking = false

    init {
        val prefs = Settings.load()
        volume = prefs.volume
        repeatMode = prefs.repeatMode
    }

    companion object {
        @Volatile private var fxStarted = false
        private fun ensureFxStarted() {
            if (!fxStarted) {
                synchronized(this) {
                    if (!fxStarted) {
                        try {
                            Platform.startup { }
                        } catch (e: IllegalStateException) { }
                        Platform.setImplicitExit(false)
                        fxStarted = true
                    }
                }
            }
        }
    }

    fun loadFolder(folder: File): Boolean {
        ensureFxStarted()
        if (!folder.exists()) folder.mkdirs()

        playlist = folder.listFiles { f -> f.isFile && f.extension.equals("mp3", ignoreCase = true) }
            ?.sortedBy { it.name }
            ?: emptyList()

        // Leer metadatos de todas las pistas una sola vez (para la biblioteca).
        tracks = playlist.map { readTrackInfo(it) }

        if (playlist.isEmpty()) {
            currentIndex = -1
            return false
        }
        loadAt(0, autoPlay = false)
        return true
    }

    /** Lee title/artist/duracion de un mp3 via tags ID3. */
    private fun readTrackInfo(mp3: File): TrackInfo {
        return try {
            val audioFile = AudioFileIO.read(mp3)
            val tag = audioFile.tag
            val t = tag?.getFirst(FieldKey.TITLE)?.takeIf { it.isNotBlank() }
                ?: mp3.nameWithoutExtension
            val a = tag?.getFirst(FieldKey.ARTIST)?.takeIf { it.isNotBlank() }
                ?: "Desconocido"
            val dur = audioFile.audioHeader?.trackLength ?: 0
            TrackInfo(title = t, artist = a, durationSeconds = dur)
        } catch (e: Exception) {
            TrackInfo(title = mp3.nameWithoutExtension, artist = "Desconocido", durationSeconds = 0)
        }
    }

    private fun loadAt(index: Int, autoPlay: Boolean) {
        if (index !in playlist.indices) return
        val mp3 = playlist[index]

        // Si el archivo fue borrado por fuera: revalidar la cola y saltar.
        if (!mp3.exists()) {
            println("loadAt: '${mp3.name}' ya no existe, revalidando cola...")
            refreshQueue()
            if (playlist.isNotEmpty()) {
                val nextIndex = index.coerceAtMost(playlist.size - 1)
                loadAt(nextIndex, autoPlay = autoPlay)
            }
            return
        }

        currentIndex = index

        player?.dispose()

        // Metadatos de la pista actual (para el TrackInfo del reproductor)
        try {
            val audioFile = AudioFileIO.read(mp3)
            val tag = audioFile.tag
            title = tag?.getFirst(FieldKey.TITLE)?.takeIf { it.isNotBlank() }
                ?: mp3.nameWithoutExtension
            artist = tag?.getFirst(FieldKey.ARTIST)?.takeIf { it.isNotBlank() }
                ?: "Desconocido"
            cover = try {
                val bytes = tag?.firstArtwork?.binaryData
                if (bytes != null && bytes.isNotEmpty())
                    SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                else null
            } catch (e: Exception) { null }
        } catch (e: Exception) {
            title = mp3.nameWithoutExtension
            artist = "Desconocido"
            cover = null
        }

        val media = Media(mp3.toURI().toString())
        val mp = MediaPlayer(media)
        mp.volume = volume.toDouble()
        mp.cycleCount = if (repeatMode == RepeatMode.ONE) MediaPlayer.INDEFINITE else 1

        mp.setOnReady { durationSeconds = mp.totalDuration.toSeconds().toInt() }
        mp.currentTimeProperty().addListener { _, _, newTime ->
            if (!seeking) {
                val total = mp.totalDuration.toSeconds()
                val cur = newTime.toSeconds()
                currentSeconds = cur.toInt()
                progress = if (total > 0) (cur / total).toFloat().coerceIn(0f, 1f) else 0f
            }
        }
        mp.setOnEndOfMedia {
            when (repeatMode) {
                RepeatMode.ONE -> { }
                RepeatMode.ALL -> {
                    val nextIndex = if (currentIndex < playlist.size - 1) currentIndex + 1 else 0
                    loadAt(nextIndex, autoPlay = true)
                }
                RepeatMode.OFF -> {
                    mp.stop(); isPlaying = false; progress = 0f; currentSeconds = 0
                }
            }
        }

        player = mp
        progress = 0f
        currentSeconds = 0
        if (autoPlay) play()
    }

    /**
     * Agrega un MP3 recien descargado a la cola de reproduccion, en caliente.
     * Devuelve el indice donde quedo, o -1 si no se pudo.
     */
    fun addToQueue(file: File): Int {
        if (!file.exists() || !file.extension.equals("mp3", ignoreCase = true)) return -1

        // Evita duplicados si ya esta en la lista
        val already = playlist.indexOfFirst { it.absolutePath == file.absolutePath }
        if (already >= 0) return already

        // Agrega a la playlist y a la lista de metadatos (biblioteca)
        playlist = playlist + file
        tracks = tracks + readTrackInfo(file)

        // Si no habia nada cargado, prepara esta como actual (sin reproducir)
        if (currentIndex == -1) {
            loadAt(playlist.size - 1, autoPlay = false)
        }
        return playlist.size - 1
    }

    /**
     * Revisa la cola y quita las pistas cuyo archivo ya no existe en disco.
     * Ajusta el indice actual y detiene si se borro la que sonaba.
     */
    fun refreshQueue() {
        if (playlist.isEmpty()) return

        // Recordar cual era el archivo actual para reubicar el indice
        val currentFile = playlist.getOrNull(currentIndex)

        // Filtrar los que siguen existiendo
        val survivors = playlist.filter { it.exists() }

        if (survivors.size == playlist.size) return   // nada cambio

        // Reconstruir playlist y tracks solo con los que existen
        val survivorTracks = survivors.map { file ->
            val oldIdx = playlist.indexOf(file)
            tracks.getOrNull(oldIdx) ?: readTrackInfo(file)
        }
        playlist = survivors
        tracks = survivorTracks

        when {
            playlist.isEmpty() -> {
                stop()
                player?.dispose()
                player = null
                currentIndex = -1
            }
            currentFile != null && currentFile.exists() -> {
                // La actual sigue existiendo: recalcular su nuevo indice
                currentIndex = playlist.indexOf(currentFile)
            }
            else -> {
                // La que sonaba fue borrada: detener y posicionar en una valida
                stop()
                val newIndex = currentIndex.coerceIn(0, playlist.size - 1)
                loadAt(newIndex, autoPlay = false)
            }
        }
    }

    /** Reproduce la pista del indice dado (desde la biblioteca). */
    fun playAt(index: Int) {
        if (index in playlist.indices) loadAt(index, autoPlay = true)
    }

    fun play() { player?.let { mp -> Platform.runLater { mp.play(); isPlaying = true } } }
    fun pause() { player?.let { mp -> Platform.runLater { mp.pause(); isPlaying = false } } }
    fun stop() {
        player?.let { mp -> Platform.runLater { mp.stop(); isPlaying = false; progress = 0f; currentSeconds = 0 } }
    }
    fun toggle() { if (isPlaying) pause() else play() }

    fun next() { if (hasNext) loadAt(currentIndex + 1, autoPlay = true) }
    fun previous() { if (hasPrevious) loadAt(currentIndex - 1, autoPlay = true) }

    fun seekTo(fraction: Float) {
        val mp = player ?: return
        val total = mp.totalDuration
        if (total == null || total.isUnknown || total.isIndefinite) return
        val f = fraction.coerceIn(0f, 1f)
        progress = f
        currentSeconds = (total.toSeconds() * f).toInt()
        seeking = true
        Platform.runLater { mp.seek(Duration.seconds(total.toSeconds() * f)); seeking = false }
    }

    fun changeVolume(value: Float) {
        val v = value.coerceIn(0f, 1f)
        volume = v
        player?.let { mp -> Platform.runLater { mp.volume = v.toDouble() } }
    }

    fun persistVolume() { Settings.save(volume, repeatMode) }

    fun cycleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        player?.let { mp ->
            Platform.runLater {
                mp.cycleCount = if (repeatMode == RepeatMode.ONE) MediaPlayer.INDEFINITE else 1
            }
        }
        Settings.save(volume, repeatMode)
    }

    fun dispose() { player?.dispose(); player = null }
}
