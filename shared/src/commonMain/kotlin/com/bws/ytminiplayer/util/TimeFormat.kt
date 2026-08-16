package com.bws.ytminiplayer.util

/** Convierte segundos a formato m:ss (por ejemplo 118 -> "1:58"). */
internal fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
