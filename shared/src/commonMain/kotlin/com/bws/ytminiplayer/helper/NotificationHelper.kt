package com.bws.ytminiplayer.helper

import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.image.BufferedImage

/**
 * Helper simple para mostrar notificaciones nativas del sistema operativo
 * (bandeja / tray) desde cualquier parte de la app.
 *
 * Uso:
 *   NotificationHelper.success("Descarga completa", "Cancion.mp3 se descargo correctamente")
 *   NotificationHelper.error("Error de descarga", "No se pudo descargar el audio")
 *   NotificationHelper.info("Descargando...", "Iniciando descarga de audio")
 */
object NotificationHelper {

    private var trayIcon: TrayIcon? = null

    /** Inicializa el icono en la bandeja del sistema (una sola vez, de forma perezosa) */
    private fun getTrayIcon(): TrayIcon? {
        if (!SystemTray.isSupported()) {
            println("NotificationHelper: SystemTray no soportado en este sistema")
            return null
        }

        if (trayIcon == null) {
            val tray = SystemTray.getSystemTray()

            // Icono transparente 16x16 por defecto (podes reemplazarlo por tu propio .png)
            val image: BufferedImage = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

            val icon = TrayIcon(image, "YT Mini Player")
            icon.isImageAutoSize = true

            try {
                tray.add(icon)
                trayIcon = icon
            } catch (e: Exception) {
                println("NotificationHelper: no se pudo agregar el icono al tray: ${e.message}")
                return null
            }
        }

        return trayIcon
    }

    fun success(title: String, message: String) = notify(title, message, TrayIcon.MessageType.INFO)

    fun error(title: String, message: String) = notify(title, message, TrayIcon.MessageType.ERROR)

    fun info(title: String, message: String) = notify(title, message, TrayIcon.MessageType.NONE)

    fun warning(title: String, message: String) = notify(title, message, TrayIcon.MessageType.WARNING)

    private fun notify(title: String, message: String, type: TrayIcon.MessageType) {
        val icon = getTrayIcon()
        if (icon != null) {
            icon.displayMessage(title, message, type)
        } else {
            // Fallback si no hay soporte de tray: al menos que quede en consola/log
            println("[$type] $title: $message")
        }
    }
}
