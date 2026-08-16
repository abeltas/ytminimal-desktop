package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.audio.RepeatMode
import com.bws.ytminiplayer.ui.icons.drawNext
import com.bws.ytminiplayer.ui.icons.drawPause
import com.bws.ytminiplayer.ui.icons.drawPlay
import com.bws.ytminiplayer.ui.icons.drawPrevious
import com.bws.ytminiplayer.ui.icons.drawRepeat
import com.bws.ytminiplayer.ui.icons.drawRepeatOne
import com.bws.ytminiplayer.ui.icons.drawVolume
import com.bws.ytminiplayer.ui.theme.PlayerColors

/* ------------------------------------------------------------------ */
/*  Controles inferiores                                               */
/*  repetir - anterior - PLAY - siguiente - volumen                    */
/* ------------------------------------------------------------------ */
@Composable
internal fun Controls(roboto: FontFamily, audio: AudioPlayerController) {
    var showVolume by remember { mutableStateOf(false) }

    val prevColor = if (audio.hasPrevious) PlayerColors.Accent else PlayerColors.TextSecondary
    val nextColor = if (audio.hasNext) PlayerColors.Accent else PlayerColors.TextSecondary
    val repeatColor = if (audio.repeatMode == RepeatMode.OFF)
        PlayerColors.TextSecondary else PlayerColors.Accent

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Repetir (cicla OFF -> ALL -> ONE, se persiste en cada clic)
        CircleControl(onClick = { audio.cycleRepeatMode() }) {
            Canvas(Modifier.size(17.dp)) {
                if (audio.repeatMode == RepeatMode.ONE) drawRepeatOne(repeatColor)
                else drawRepeat(repeatColor)
            }
        }

        // Anterior
        CircleControl(onClick = { audio.previous() }) {
            Canvas(Modifier.size(15.dp)) { drawPrevious(prevColor) }
        }

        // Play / Pausa
        IconButton(
            onClick = { audio.toggle() },
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(PlayerColors.Accent, PlayerColors.AccentGradientEnd)
                    )
                )
        ) {
            Canvas(Modifier.size(24.dp)) {
                if (audio.isPlaying) drawPause(Color.White) else drawPlay(Color.White)
            }
        }

        // Siguiente
        CircleControl(onClick = { audio.next() }) {
            Canvas(Modifier.size(15.dp)) { drawNext(nextColor) }
        }

        // Volumen con slider en Popup
        Box {
            CircleControl(onClick = { showVolume = !showVolume }) {
                Canvas(Modifier.size(18.dp)) {
                    drawVolume(PlayerColors.Accent, muted = audio.volume <= 0f)
                }
            }
            if (showVolume) {
                Popup(
                    offset = IntOffset(x = -60, y = -50),
                    onDismissRequest = { showVolume = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnClickOutside = true,
                        dismissOnBackPress = true
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PlayerColors.ControlCircle)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        VolumeSlider(
                            value = audio.volume,
                            onChange = { audio.changeVolume(it) },
                            onRelease = { audio.persistVolume() }   // guarda al soltar
                        )
                    }
                }
            }
        }
    }
}

/* Slider de volumen: cambia mientras arrastra, persiste al soltar. */
@Composable
private fun VolumeSlider(
    value: Float,
    onChange: (Float) -> Unit,
    onRelease: () -> Unit
) {
    Canvas(
        modifier = Modifier
            .width(80.dp)
            .height(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onChange((offset.x / size.width).coerceIn(0f, 1f))
                        onRelease()   // un tap tambien persiste
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = { onRelease() },      // al soltar, persiste
                    onDragCancel = { onRelease() }
                ) { change, _ ->
                    change.consume()
                    onChange((change.position.x / size.width).coerceIn(0f, 1f))
                }
            }
    ) {
        val y = size.height / 2
        val trackW = size.width
        val filledW = trackW * value.coerceIn(0f, 1f)
        drawLine(
            color = PlayerColors.TrackInactive,
            start = Offset(0f, y), end = Offset(trackW, y),
            strokeWidth = 3f, cap = StrokeCap.Round
        )
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(PlayerColors.AccentGradientEnd, PlayerColors.Accent)
            ),
            start = Offset(0f, y), end = Offset(filledW, y),
            strokeWidth = 3f, cap = StrokeCap.Round
        )
        drawCircle(
            color = PlayerColors.Accent,
            radius = 5f,
            center = Offset(filledW, y)
        )
    }
}
