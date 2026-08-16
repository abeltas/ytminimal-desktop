package com.bws.ytminiplayer.ui.icons

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.bws.ytminiplayer.ui.theme.PlayerColors

/* ------------------------------------------------------------------ */
/*  Iconos dibujados a mano (sin material-icons-extended)              */
/* ------------------------------------------------------------------ */

internal fun DrawScope.drawMusicNote(c: Color) {
    val w = size.width; val h = size.height; val s = 1.8f
    drawLine(c, Offset(w * 0.68f, h * 0.15f), Offset(w * 0.68f, h * 0.72f), s, StrokeCap.Round)
    drawCircle(c, radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.72f))
}

internal fun DrawScope.drawStar(c: Color) {
    val w = size.width; val cx = w / 2; val cy = w / 2
    val rOuter = w * 0.42f; val rInner = w * 0.17f
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) rOuter else rInner
        val angle = Math.toRadians((-90 + i * 36).toDouble())
        val x = cx + r * Math.cos(angle).toFloat()
        val y = cy + r * Math.sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, c, style = Stroke(width = 1.7f, join = StrokeJoin.Round))
}

internal fun DrawScope.drawDownload(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    drawLine(c, Offset(w / 2, h * 0.12f), Offset(w / 2, h * 0.72f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.28f, h * 0.5f), Offset(w / 2, h * 0.75f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.72f, h * 0.5f), Offset(w / 2, h * 0.75f), s, StrokeCap.Round)
}

internal fun DrawScope.drawEqualizer(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f; val mid = h / 2
    drawLine(c, Offset(0f, mid), Offset(w * 0.22f, mid), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.22f, mid), Offset(w * 0.34f, h * 0.15f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.34f, h * 0.15f), Offset(w * 0.5f, h * 0.9f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.5f, h * 0.9f), Offset(w * 0.62f, mid), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.62f, mid), Offset(w, mid), s, StrokeCap.Round)
}

internal fun DrawScope.drawList(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    val dotX = w * 0.08f; val lineStart = w * 0.28f
    val ys = listOf(h * 0.22f, h * 0.5f, h * 0.78f)
    for (y in ys) {
        drawCircle(c, radius = 1.1f, center = Offset(dotX, y))
        drawLine(c, Offset(lineStart, y), Offset(w * 0.95f, y), s, StrokeCap.Round)
    }
}

internal fun DrawScope.drawChevronDown(c: Color) {
    val w = size.width; val h = size.height; val s = 1.8f
    drawLine(c, Offset(w * 0.28f, h * 0.4f), Offset(w * 0.5f, h * 0.62f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.72f, h * 0.4f), Offset(w * 0.5f, h * 0.62f), s, StrokeCap.Round)
}

/* Repetir (dos trazos en escalon con flechas opuestas). */
internal fun DrawScope.drawRepeat(c: Color) {
    val w = size.width; val h = size.height; val s = 2.0f
    drawLine(c, Offset(w * 0.12f, h * 0.42f), Offset(w * 0.12f, h * 0.22f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.12f, h * 0.22f), Offset(w * 0.78f, h * 0.22f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.66f, h * 0.10f), Offset(w * 0.88f, h * 0.22f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.66f, h * 0.34f), Offset(w * 0.88f, h * 0.22f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.88f, h * 0.58f), Offset(w * 0.88f, h * 0.78f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.88f, h * 0.78f), Offset(w * 0.22f, h * 0.78f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.34f, h * 0.66f), Offset(w * 0.12f, h * 0.78f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.34f, h * 0.90f), Offset(w * 0.12f, h * 0.78f), s, StrokeCap.Round)
}

/* Repetir UNA: igual que drawRepeat pero con un "1" pequeno en el centro. */
internal fun DrawScope.drawRepeatOne(c: Color) {
    drawRepeat(c)
    val w = size.width; val h = size.height; val s = 1.6f
    // "1" dibujado con dos trazos en el centro del icono
    val x = w * 0.50f
    // asta vertical
    drawLine(c, Offset(x, h * 0.40f), Offset(x, h * 0.60f), s, StrokeCap.Round)
    // pie / serif inferior
    drawLine(c, Offset(w * 0.44f, h * 0.60f), Offset(w * 0.56f, h * 0.60f), s, StrokeCap.Round)
    // banderin superior
    drawLine(c, Offset(w * 0.44f, h * 0.45f), Offset(x, h * 0.40f), s, StrokeCap.Round)
}

internal fun DrawScope.drawShuffle(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    drawLine(c, Offset(w * 0.08f, h * 0.25f), Offset(w * 0.92f, h * 0.75f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.08f, h * 0.75f), Offset(w * 0.92f, h * 0.25f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.92f, h * 0.25f), Offset(w * 0.74f, h * 0.20f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.92f, h * 0.25f), Offset(w * 0.82f, h * 0.40f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.92f, h * 0.75f), Offset(w * 0.74f, h * 0.80f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.92f, h * 0.75f), Offset(w * 0.82f, h * 0.60f), s, StrokeCap.Round)
}

internal fun DrawScope.drawPause(c: Color) {
    val w = size.width; val h = size.height
    val barW = w * 0.22f; val gap = w * 0.16f
    drawRoundRectBar(c, x = w / 2 - gap / 2 - barW, top = h * 0.12f, width = barW, bottom = h * 0.88f)
    drawRoundRectBar(c, x = w / 2 + gap / 2, top = h * 0.12f, width = barW, bottom = h * 0.88f)
}

private fun DrawScope.drawRoundRectBar(c: Color, x: Float, top: Float, width: Float, bottom: Float) {
    drawRoundRect(
        color = c, topLeft = Offset(x, top), size = Size(width, bottom - top),
        cornerRadius = CornerRadius(width * 0.4f, width * 0.4f)
    )
}

internal fun DrawScope.drawPlay(c: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * 0.28f, h * 0.15f)
        lineTo(w * 0.82f, h * 0.5f)
        lineTo(w * 0.28f, h * 0.85f)
        close()
    }
    drawPath(path, c)
}

/* Anterior: triangulo apuntando a la izquierda + barra vertical. */
internal fun DrawScope.drawPrevious(c: Color) {
    val w = size.width; val h = size.height
    drawRoundRect(
        color = c,
        topLeft = Offset(w * 0.18f, h * 0.25f),
        size = Size(w * 0.10f, h * 0.5f),
        cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
    )
    val path = Path().apply {
        moveTo(w * 0.82f, h * 0.22f)
        lineTo(w * 0.38f, h * 0.5f)
        lineTo(w * 0.82f, h * 0.78f)
        close()
    }
    drawPath(path, c)
}

/* Siguiente: triangulo apuntando a la derecha + barra vertical. */
internal fun DrawScope.drawNext(c: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * 0.18f, h * 0.22f)
        lineTo(w * 0.62f, h * 0.5f)
        lineTo(w * 0.18f, h * 0.78f)
        close()
    }
    drawPath(path, c)
    drawRoundRect(
        color = c,
        topLeft = Offset(w * 0.72f, h * 0.25f),
        size = Size(w * 0.10f, h * 0.5f),
        cornerRadius = CornerRadius(w * 0.03f, w * 0.03f)
    )
}

/* Icono minimizar: guion horizontal. */
internal fun DrawScope.drawMinimize(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    drawLine(c, Offset(w * 0.2f, h * 0.55f), Offset(w * 0.8f, h * 0.55f), s, StrokeCap.Round)
}

/* Icono cerrar: aspa. */
internal fun DrawScope.drawClose(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    drawLine(c, Offset(w * 0.22f, h * 0.22f), Offset(w * 0.78f, h * 0.78f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.78f, h * 0.22f), Offset(w * 0.22f, h * 0.78f), s, StrokeCap.Round)
}

/* Icono de volumen (altavoz + ondas; X cuando esta en 0). */
internal fun DrawScope.drawVolume(c: Color, muted: Boolean = false) {
    val w = size.width; val h = size.height; val s = 1.7f
    val boxLeft = w * 0.15f
    val boxRight = w * 0.35f
    val boxTop = h * 0.38f
    val boxBottom = h * 0.62f
    val coneTip = w * 0.55f
    val path = Path().apply {
        moveTo(boxLeft, boxTop)
        lineTo(boxRight, boxTop)
        lineTo(coneTip, h * 0.22f)
        lineTo(coneTip, h * 0.78f)
        lineTo(boxRight, boxBottom)
        lineTo(boxLeft, boxBottom)
        close()
    }
    drawPath(path, c, style = Stroke(width = s, join = StrokeJoin.Round))
    if (muted) {
        drawLine(c, Offset(w * 0.66f, h * 0.38f), Offset(w * 0.88f, h * 0.62f), s, StrokeCap.Round)
        drawLine(c, Offset(w * 0.88f, h * 0.38f), Offset(w * 0.66f, h * 0.62f), s, StrokeCap.Round)
    } else {
        drawArc(
            color = c, startAngle = -45f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(w * 0.5f, h * 0.28f),
            size = Size(w * 0.28f, h * 0.44f),
            style = Stroke(width = s, cap = StrokeCap.Round)
        )
        drawArc(
            color = c, startAngle = -45f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(w * 0.5f, h * 0.16f),
            size = Size(w * 0.44f, h * 0.68f),
            style = Stroke(width = s, cap = StrokeCap.Round)
        )
    }
}

/* Icono de lupa (buscar). */
internal fun DrawScope.drawSearch(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    val cx = w * 0.42f
    val cy = h * 0.42f
    val r = w * 0.26f
    drawCircle(color = c, radius = r, center = Offset(cx, cy), style = Stroke(width = s))
    val startX = cx + r * 0.70f
    val startY = cy + r * 0.70f
    drawLine(c, Offset(startX, startY), Offset(w * 0.82f, h * 0.82f), s + 0.4f, StrokeCap.Round)
}

/* Caratula por defecto (cuando el MP3 no trae imagen embebida). */
internal fun DrawScope.drawDefaultCover() {
    drawRect(
        brush = Brush.linearGradient(
            listOf(
                PlayerColors.AccentGradientEnd.copy(alpha = 0.35f),
                PlayerColors.CardTop
            )
        )
    )
    val cx = size.width / 2
    val cy = size.height / 2
    val scale = size.minDimension
    val s = scale * 0.05f
    drawLine(
        PlayerColors.Accent,
        Offset(cx + scale * 0.14f, cy - scale * 0.22f),
        Offset(cx + scale * 0.14f, cy + scale * 0.12f),
        s, StrokeCap.Round
    )
    drawCircle(
        PlayerColors.Accent,
        radius = scale * 0.11f,
        center = Offset(cx + scale * 0.02f, cy + scale * 0.14f)
    )
}

/* Flecha atras (para cerrar el popup de biblioteca). */
internal fun DrawScope.drawBack(c: Color) {
    val w = size.width; val h = size.height; val s = 1.9f
    // Linea horizontal
    drawLine(c, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.82f, h * 0.5f), s, StrokeCap.Round)
    // Punta de flecha a la izquierda
    drawLine(c, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.42f, h * 0.32f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.22f, h * 0.5f), Offset(w * 0.42f, h * 0.68f), s, StrokeCap.Round)
}

/* Sincronizar: dos flechas circulares (refresh). */
internal fun DrawScope.drawSync(c: Color) {
    val w = size.width; val h = size.height; val s = 1.7f
    val inset = w * 0.16f
    val arcSize = Size(w - inset * 2, h - inset * 2)

    // Arco superior (deja hueco a la derecha para la flecha)
    drawArc(
        color = c, startAngle = 300f, sweepAngle = 200f, useCenter = false,
        topLeft = Offset(inset, inset), size = arcSize,
        style = Stroke(width = s, cap = StrokeCap.Round)
    )
    // Flecha del arco superior (extremo derecho, apuntando hacia arriba-derecha)
    drawLine(c, Offset(w * 0.80f, h * 0.16f), Offset(w * 0.86f, h * 0.34f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.86f, h * 0.34f), Offset(w * 0.68f, h * 0.30f), s, StrokeCap.Round)

    // Arco inferior (deja hueco a la izquierda para la flecha)
    drawArc(
        color = c, startAngle = 120f, sweepAngle = 200f, useCenter = false,
        topLeft = Offset(inset, inset), size = arcSize,
        style = Stroke(width = s, cap = StrokeCap.Round)
    )
    // Flecha del arco inferior (extremo izquierdo, apuntando hacia abajo-izquierda)
    drawLine(c, Offset(w * 0.20f, h * 0.84f), Offset(w * 0.14f, h * 0.66f), s, StrokeCap.Round)
    drawLine(c, Offset(w * 0.14f, h * 0.66f), Offset(w * 0.32f, h * 0.70f), s, StrokeCap.Round)
}
