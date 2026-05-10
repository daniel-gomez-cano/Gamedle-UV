package com.gamedleuv.data.remote.api

data class GameResponse(
    val id: Int,
    val name: String,
    val cover: Cover?
)

data class Cover(
    val url: String
)