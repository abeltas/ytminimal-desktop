package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bws.ytminiplayer.audio.MusicFolder
import com.bws.ytminiplayer.data.ThumbCache
import com.bws.ytminiplayer.data.YouTubeApi
import com.bws.ytminiplayer.data.YtVideo
import com.bws.ytminiplayer.ui.icons.drawDownload
import com.bws.ytminiplayer.ui.icons.drawSync
import com.bws.ytminiplayer.ui.theme.PlayerColors
import com.bws.ytminiplayer.util.formatTime
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import com.bws.ytminiplayer.audio.AudioPlayerController
import com.bws.ytminiplayer.helper.YouTubeAudioDownloader
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.bws.ytminiplayer.helper.NotificationHelper
import kotlinx.coroutines.delay
import java.io.File

@Composable
internal fun SearchView(
    roboto: FontFamily,
    audio: AudioPlayerController,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<YtVideo>>(emptyList()) }
    var searched by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    // Autofocus al mostrar la vista
    LaunchedEffect(Unit) {
        delay(500)
        focusRequester.requestFocus()
    }

    fun sync() {
        if (loading) return
        loading = true
        searched = true
        scope.launch {
            val term = query.ifBlank { "mana" }
            val all = YouTubeApi.search(term)
            // Ocultar los que ya existen como mp3 en la carpeta
            results = all.filterNot { MusicFolder.existsMp3(it.videoId) }
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(PlayerColors.CardTop)
            .padding(vertical = 12.dp)
    ) {
        // Barra de busqueda
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlayerColors.ControlCircle)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    enabled = !loading,
                    modifier = Modifier.focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = if (loading) PlayerColors.TextSecondary else PlayerColors.TextPrimary,
                        fontFamily = roboto,
                        fontSize = 13.sp
                    ),
                    cursorBrush = SolidColor(PlayerColors.Accent),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { sync() }),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Buscar musica...",
                                color = PlayerColors.TextSecondary,
                                fontFamily = roboto,
                                fontSize = 13.sp
                            )
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PlayerColors.ControlCircle)
                    .clickable(enabled = !loading) { sync() },
                contentAlignment = Alignment.Center
            ) {
                val syncColor = if (loading) PlayerColors.TextSecondary else PlayerColors.Accent
                Canvas(Modifier.size(16.dp)) { drawSync(syncColor) }
            }
        }

        Spacer(Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when {
                loading -> Text(
                    text = "Buscando...",
                    color = PlayerColors.TextSecondary,
                    fontFamily = roboto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
                results.isEmpty() -> Text(
                    text = if (searched) "Sin resultados" else "Escribe y pulsa sincronizar",
                    color = PlayerColors.TextSecondary,
                    fontFamily = roboto,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(results) { _, item ->
                        SearchRow(
                            roboto = roboto,
                            video = item,
                            onDownloadRequest = { video ->
                                try {
                                     val complete = YouTubeAudioDownloader.downloadAudio(
                                        video,
                                        onProgress = { progress, status ->
                                            println("Descargando => $progress, $status")
                                        }
                                     )
                                    complete
                                } catch (e: Exception) {
                                    println("Descarga fallida: ${e.message}")
                                    false
                                }
                            },
                            onDownloaded = { videoId ->
                                val folder = MusicFolder.resolve()
                                println("OnDownloaded Event:folder ${folder.absolutePath},  $videoId")
                                val mp3 = File(folder, "${videoId}.mp3")
                                println("OnDownloaded Event:mp3 ${mp3.absolutePath}, ${mp3.isFile()}")
                                audio.addToQueue(mp3)
                                results = results.filterNot { it.videoId == videoId }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchRow(
    roboto: FontFamily,
    video: YtVideo,
    onDownloadRequest: suspend (YtVideo) -> Boolean,  // devuelve true si ok, false si error
    onDownloaded: (String) -> Unit                    // avisa al padre para quitarlo de la lista
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val rowBg = if (isHovered) PlayerColors.ControlCircle.copy(alpha = 0.5f) else Color.Transparent

    var thumb by remember(video.videoId) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(video.videoId) {
        thumb = ThumbCache.getThumbnail(video.videoId, video.thumbnailUrl)
    }

    // Estado de descarga de ESTA fila
    var downloading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBg)
            .hoverable(interactionSource)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PlayerColors.ControlCircle)
                .clickable(enabled = !downloading) {
                    downloading = true
                    scope.launch {
                        println("Downloading start ${video.videoId}")
                        val ok = onDownloadRequest(video)
                        println("Downloading result = ${ok}")
                        if (ok) {
                            onDownloaded(video.videoId)
                            NotificationHelper.success(
                                "Descarga completa",
                                "\"${video.title}\" se descargó correctamente"
                            )
                        } else {
                            NotificationHelper.error(
                                "Error al descargar",
                                "No se pudo descargar \"${video.title}\""
                            )
                        }
                        println("Downloading end ${video.videoId}")
                        downloading = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Miniatura de fondo
            thumb?.let {
                Image(
                    bitmap = it,
                    contentDescription = "Miniatura",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Velo oscuro
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )
            // Contenido encima: progressbar mientras descarga, o icono de descarga
            if (downloading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Canvas(Modifier.size(16.dp)) { drawDownload(Color.White) }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                color = PlayerColors.TextPrimary,
                fontFamily = roboto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = video.artist,
                color = PlayerColors.TextSecondary,
                fontFamily = roboto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = formatTime(0),
            color = PlayerColors.TextSecondary,
            fontFamily = roboto,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
