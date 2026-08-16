package com.bws.ytminiplayer.config

/**
 * Valores de configuracion de la API de YouTube.
 *
 * NOTA DE SEGURIDAD: la API key esta incrustada aqui solo para desarrollo.
 * Para una app distribuida, no dejes la key en el codigo fuente: usala desde
 * una variable de entorno o un archivo de config fuera del control de versiones.
 * Regenera la key en Google Cloud Console si se ha expuesto publicamente.
 */
object YouTubeConfig {

    /** Base del endpoint de busqueda de la Data API v3. */
    const val BASE_URL = "https://www.googleapis.com/youtube/v3/search"

    /** API key de YouTube Data API v3. */
    const val API_KEY = "AIzaSyDg3CJs70b6ScLWd4D1HzQWyADqUpPA86c"

    /** Parametros fijos de la busqueda. */
    const val PART = "snippet"
    const val TYPE = "video"
    const val MAX_RESULTS = 15
    const val VIDEO_CATEGORY_ID = 10

    /**
     * Construye la URL de busqueda a partir de la query del usuario.
     * Codifica la query para que espacios y caracteres especiales sean validos.
     */
    fun buildSearchUrl(query: String): String {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        return "$BASE_URL" +
            "?part=$PART" +
            "&q=$encoded" +
            "&type=$TYPE" +
            "&videoCategoryId=$VIDEO_CATEGORY_ID" +
            "&maxResults=$MAX_RESULTS" +
            "&key=$API_KEY"
    }
}
