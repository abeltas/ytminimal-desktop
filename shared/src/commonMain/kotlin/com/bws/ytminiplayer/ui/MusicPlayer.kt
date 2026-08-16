package com.bws.ytminiplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.ui.theme.PlayerColors
import com.bws.ytminiplayer.ui.theme.robotoFamily

/* ------------------------------------------------------------------ */
/*  Reproductor completo.                                              */
/*  Overlays sobre el contenido central: Biblioteca y Busqueda.        */
/* ------------------------------------------------------------------ */
@Composable
fun MusicPlayer(
    windowScope: FrameWindowScope,
    audio: AudioPlayerController,
    onMinimize: () -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val roboto = robotoFamily()
    var showLibrary by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(
                Brush.verticalGradient(
                    listOf(PlayerColors.CardTop, PlayerColors.CardBottom)
                )
            )
            .padding(horizontal = 20.dp, vertical = 17.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopBar(
                windowScope = windowScope,
                roboto = roboto,
                onMinimize = onMinimize,
                onClose = onClose,
                onToggleLibrary = {
                    showLibrary = !showLibrary
                    if (showLibrary) showSearch = false   // solo un overlay a la vez
                },
                onToggleSearch = {
                    showSearch = !showSearch
                    if (showSearch) showLibrary = false
                }
            )

            // Zona central: contenido normal, o un overlay (biblioteca / busqueda).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 24.dp)
            ) {
                when {
                    showLibrary -> LibraryView(
                        roboto = roboto,
                        audio = audio,
                        onClose = {
                            audio.refreshQueue()
                            showLibrary = false
                        }
                    )
                    showSearch -> SearchView(
                        roboto = roboto,
                        audio = audio,
                        onClose = {
                            audio.refreshQueue()
                            showSearch = false
                        }
                    )
                    else -> Column(modifier = Modifier.fillMaxSize()) {
                        // TrackInfo centrado verticalmente en el espacio disponible
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f)/*.padding(top = 10.dp)*/,
                            contentAlignment = Alignment.Center
                        ) {
                            TrackInfo(roboto, audio)
                        }
                        // ProgressSection pegado abajo
                        ProgressSection(roboto, audio)
                    }
                }
            }

            Controls(roboto, audio)
        }
    }
}
