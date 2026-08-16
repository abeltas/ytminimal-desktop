package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.ui.icons.drawPlay
import com.bws.ytminiplayer.ui.theme.PlayerColors
import com.bws.ytminiplayer.util.formatTime
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/* ------------------------------------------------------------------ */
/*  Biblioteca: overlay con la lista de canciones de la carpeta.       */
/*  Cada fila: boton play - titulo/artista - duracion.                 */
/*  La fila en reproduccion se resalta en color de acento.             */
/* ------------------------------------------------------------------ */
@Composable
internal fun LibraryView(
    roboto: FontFamily,
    audio: AudioPlayerController,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(PlayerColors.CardTop)
    ) {

        // Lista scrolleable
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(audio.tracks) { index, track ->
                val isCurrent = index == audio.currentIndex
                TrackRow(
                    roboto = roboto,
                    title = track.title,
                    artist = track.artist,
                    durationSeconds = track.durationSeconds,
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && audio.isPlaying,   // ← suena solo si es la actual Y está reproduciendo
                    onPlay = { audio.playAt(index) }
                )
            }
        }
    }
}

@Composable
private fun TrackRow(
    roboto: FontFamily,
    title: String,
    artist: String,
    durationSeconds: Int,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit
) {
    // Estado de hover de la fila
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Colores: acento solo si se reproduce; blanco/gris en el resto.
    val titleColor = if (isCurrent) PlayerColors.Accent else PlayerColors.TextPrimary
    val artistColor = if (isCurrent) PlayerColors.AccentSoft else PlayerColors.TextSecondary
    val playColor = if (isCurrent) PlayerColors.Accent else PlayerColors.TextPrimary

    // Fondo: si se reproduce, resaltado fijo; si no, translucido al hacer hover.
    val rowBg = when {
        isCurrent -> PlayerColors.ControlCircle
        isHovered -> PlayerColors.ControlCircle.copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBg)
            .hoverable(interactionSource)          // detecta hover, sin clic
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Boton play: UNICO elemento que dispara el evento
        // Boton play / ecualizador animado si se reproduce
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PlayerColors.ControlCircle)
                .clickable { onPlay() },
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent && isPlaying) {
                AnimatedEqualizer(
                    color = PlayerColors.Accent,
                    modifier = Modifier.size(15.dp)
                )
            } else {
                Canvas(Modifier.size(15.dp)) { drawPlay(playColor) }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = titleColor,
                fontFamily = roboto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = artist,
                color = artistColor,
                fontFamily = roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = formatTime(durationSeconds),
            color = PlayerColors.TextSecondary,
            fontFamily = roboto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AnimatedEqualizer(
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "eq")

    // Tres barras con fases distintas para que no suban a la vez
    val bar1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 1f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "b3"
    )

    Canvas(modifier = modifier) {
        val heights = listOf(bar1, bar2, bar3)
        val barW = size.width * 0.18f
        val gap = (size.width - barW * 3) / 2f
        heights.forEachIndexed { i, frac ->
            val h = size.height * frac
            val x = i * (barW + gap)
            val top = size.height - h
            drawRoundRect(
                color = color,
                topLeft = Offset(x, top),
                size = Size(barW, h),
                cornerRadius = CornerRadius(barW / 2, barW / 2)
            )
        }
    }
}
