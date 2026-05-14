package com.gamedleuv.domain.model

data class Game(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    // Campos para la pista (RF-05)
    val releaseYear: String?,
    val publisher: String?,
    val genre: String?
)