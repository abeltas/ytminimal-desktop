package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import com.bws.ytminiplayer.ui.icons.drawClose
import com.bws.ytminiplayer.ui.icons.drawDownload
import com.bws.ytminiplayer.ui.icons.drawEqualizer
import com.bws.ytminiplayer.ui.icons.drawList
import com.bws.ytminiplayer.ui.icons.drawMinimize
import com.bws.ytminiplayer.ui.icons.drawMusicNote
import com.bws.ytminiplayer.ui.icons.drawSearch
import com.bws.ytminiplayer.ui.theme.PlayerColors

/* ------------------------------------------------------------------ */
/*  Barra superior: logo + iconos + controles de ventana.              */
/*  Iconos: descarga - ecualizador(tenue) - buscar - lista.            */
/*  (El favorito/estrella fue eliminado.)                              */
/* ------------------------------------------------------------------ */
@Composable
internal fun TopBar(
    windowScope: FrameWindowScope,
    roboto: FontFamily,
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onToggleLibrary: () -> Unit,
    onToggleSearch: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        windowScope.WindowDraggableArea(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(PlayerColors.Accent, PlayerColors.AccentGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(14.dp)) { drawMusicNote(Color.White) }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "YT Minimal",
                    color = PlayerColors.Accent,
                    fontFamily = roboto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CanvasIconButton(onClick = {}) { drawDownload(PlayerColors.TextSecondary) }
            CanvasIconButton(onClick = {}) { drawEqualizer(PlayerColors.TextSecondary) }
            CanvasIconButton(onClick = onToggleSearch) { drawSearch(PlayerColors.Accent) }
            CanvasIconButton(onClick = onToggleLibrary) { drawList(PlayerColors.Accent) }
            Spacer(Modifier.width(4.dp))
            CanvasIconButton(onClick = onMinimize, iconSize = 14) { drawMinimize(PlayerColors.WindowIcon) }
            CanvasIconButton(onClick = onClose, iconSize = 14) { drawClose(PlayerColors.WindowIcon) }
        }
    }
}
