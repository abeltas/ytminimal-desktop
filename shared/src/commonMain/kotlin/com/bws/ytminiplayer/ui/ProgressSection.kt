package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.ui.theme.PlayerColors
import com.bws.ytminiplayer.util.formatTime

/* ------------------------------------------------------------------ */
/*  Barra de progreso + tiempos                                        */
/*  Lee el progreso del audio y hace seek al arrastrar.                */
/* ------------------------------------------------------------------ */
@Composable
internal fun ProgressSection(
    roboto: FontFamily,
    audio: AudioPlayerController
) {
    val progress = audio.progress
    val currentSeconds = audio.currentSeconds
    val durationSeconds = audio.durationSeconds

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        audio.seekTo((offset.x / size.width).coerceIn(0f, 1f))
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        audio.seekTo((change.position.x / size.width).coerceIn(0f, 1f))
                    }
                }
        ) {
            val y = size.height / 2
            val trackW = size.width
            val filledW = trackW * progress.coerceIn(0f, 1f)
            drawLine(
                color = PlayerColors.TrackInactive,
                start = Offset(0f, y),
                end = Offset(trackW, y),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(PlayerColors.AccentGradientEnd, PlayerColors.Accent)
                ),
                start = Offset(0f, y),
                end = Offset(filledW, y),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = PlayerColors.Accent,
                radius = 6.3f,
                center = Offset(filledW, y)
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                formatTime(currentSeconds),
                color = PlayerColors.TextSecondary,
                fontFamily = roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                formatTime(durationSeconds),
                color = PlayerColors.TextSecondary,
                fontFamily = roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
