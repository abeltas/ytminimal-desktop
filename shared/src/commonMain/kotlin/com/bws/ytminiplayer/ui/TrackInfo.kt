package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.ui.icons.drawChevronDown
import com.bws.ytminiplayer.ui.theme.PlayerColors
import kotlinytminiplayer.shared.generated.resources.Res
import kotlinytminiplayer.shared.generated.resources.default_cover
import org.jetbrains.compose.resources.painterResource

/* ------------------------------------------------------------------ */
/*  Caratula + titulo + artista                                        */
/* ------------------------------------------------------------------ */
@Composable
internal fun TrackInfo(roboto: FontFamily, audio: AudioPlayerController) {
    Row(
        //modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Caratula: la embebida del MP3 si existe; si no, la imagen del drawable.
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(7.dp))
        ) {
            val cover = audio.cover
            if (cover != null) {
                Image(
                    bitmap = cover,
                    contentDescription = "Caratula",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.default_cover),
                    contentDescription = "Caratula por defecto",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = audio.title.ifBlank { "Sin cancion" },
                    color = PlayerColors.TextPrimary,
                    fontFamily = roboto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1
                )
                Spacer(Modifier.width(6.dp))
                //Canvas(Modifier.size(17.dp)) { drawChevronDown(PlayerColors.AccentSoft) }
            }
            Text(
                text = audio.artist.uppercase(),
                color = PlayerColors.TextSecondary,
                fontFamily = roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                maxLines = 1
            )
        }
    }
}