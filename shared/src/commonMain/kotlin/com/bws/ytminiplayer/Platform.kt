package com.bws.ytminiplayer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform