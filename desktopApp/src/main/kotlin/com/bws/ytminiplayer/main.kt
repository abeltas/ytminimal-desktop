package com.bws.ytminiplayer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.audio.MusicFolder
import com.bws.ytminiplayer.ui.MusicPlayer
import com.bws.ytminiplayer.ui.state.PlaybackState
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

fun main() = application {
    // Tamaño de la ventana
    val windowWidth = 425.dp
    val windowHeight = 380.dp

    val screen = Toolkit.getDefaultToolkit().screenSize
    val insets = Toolkit.getDefaultToolkit().getScreenInsets(
        GraphicsEnvironment.getLocalGraphicsEnvironment()
            .defaultScreenDevice.defaultConfiguration
    )
    val usableWidth = screen.width - insets.left - insets.right
    val usableHeight = screen.height - insets.top - insets.bottom
    val margin = 12

    val x = (usableWidth - windowWidth.value.toInt() - margin).coerceAtLeast(0)
    val y = (usableHeight - windowHeight.value.toInt() - margin).coerceAtLeast(0)

    val windowState: WindowState = rememberWindowState(
        width = windowWidth,
        height = windowHeight,
        position = WindowPosition(x.dp, y.dp)
    )

    // Controller de audio: una sola instancia, fuera de la UI.
    val audio = remember { AudioPlayerController() }

    // Carga la carpeta completa (playlist) al arrancar.
    DisposableEffect(Unit) {
        val folder = MusicFolder.resolve()
        val ok = audio.loadFolder(folder)
        if (!ok) {
            println("No se encontro ningun .mp3 en: ${folder.absolutePath}")
        }
        onDispose { audio.dispose() }
    }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "KotlinYTMiniPlayer",
        undecorated = true,
        transparent = true,
        resizable = false
    ) {
        MaterialTheme {
            MusicPlayer(
                windowScope = this,
                audio = audio,
                onMinimize = { windowState.isMinimized = true },
                onClose = ::exitApplication
            )
        }
    }
}