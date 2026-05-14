package com.gamedleuv.data.remote.api

data class GameResponse(
    val id: Int,
    val name: String,
    val cover: Cover?,
    // Timestamp Unix en segundos → lo convertimos a año en el repositorio
    val first_release_date: Long?,
    val genres: List<Genre>?,
    val involved_companies: List<InvolvedCompany>?
)

data class Cover(
    val url: String
)

data class Genre(
    val name: String
)

data class InvolvedCompany(
    val company: Company?,
    val publisher: Boolean
)

data class Company(
    val name: String
)