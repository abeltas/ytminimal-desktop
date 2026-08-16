package com.bws.ytminiplayer.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelos para deserializar la respuesta de YouTube Data API v3 /search.
 * Solo declaramos los campos que usamos; ignoreUnknownKeys hace el resto.
 */

@Serializable
data class YtSearchResponse(
    val items: List<YtItem> = emptyList()
)

@Serializable
data class YtItem(
    val id: YtId? = null,
    val snippet: YtSnippet? = null
)

@Serializable
data class YtId(
    @SerialName("videoId") val videoId: String? = null
)

@Serializable
data class YtSnippet(
    val title: String = "",
    @SerialName("channelTitle") val channelTitle: String = "",
    val thumbnails: YtThumbnails? = null
)

@Serializable
data class YtThumbnails(
    val default: YtThumb? = null,
    val medium: YtThumb? = null,
    val high: YtThumb? = null
)

@Serializable
data class YtThumb(
    val url: String = ""
)
