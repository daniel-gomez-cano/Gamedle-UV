package com.gamedleuv.data.repository

import android.util.Log
import com.gamedleuv.data.remote.api.IgdbApiService
import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.repository.GameRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

class GameRepositoryImpl(
    private val api: IgdbApiService
) : GameRepository {

    override suspend fun searchGames(query: String): List<Game> {
        val bodyString = """
            search "$query";
            fields name, cover.url;
            limit 10;
        """.trimIndent()

        val body = bodyString.toRequestBody("text/plain".toMediaType())

        return api.searchGames(body = body)
            .mapNotNull {
                val name = it.name ?: return@mapNotNull null
                Game(
                    id = it.id,
                    name = name,
                    imageUrl = it.cover?.url?.let { url ->
                        "https:$url".replace("t_thumb", "t_cover_big")
                    },
                    releaseYear = null,
                    publisher = null,
                    genre = null
                )
            }
    }

    override suspend fun getRandomGame(): Game? {
        val offset = (0..3000).random()

        val bodyString = """
            fields name, cover.url, first_release_date,
                   genres.name,
                   involved_companies.company.name,
                   involved_companies.publisher;
            where cover != null & rating_count > 30;
            limit 1;
            offset $offset;
        """.trimIndent()

        val body = bodyString.toRequestBody("text/plain".toMediaType())

        return api.searchGames(body = body)
            .mapNotNull { response ->
                val name = response.name ?: return@mapNotNull null

                // Convertir timestamp Unix → año
                val year = response.first_release_date?.let { timestamp ->
                    Calendar.getInstance().apply {
                        timeInMillis = timestamp * 1000L
                    }.get(Calendar.YEAR).toString()
                }

                // Primer género disponible
                val genre = response.genres?.firstOrNull()?.name

                // Buscar la empresa con publisher == true
                val publisher = response.involved_companies
                    ?.firstOrNull { it.publisher }
                    ?.company?.name

                Log.d("GAME_DEBUG", "Hint data → year=$year, genre=$genre, publisher=$publisher")

                Game(
                    id = response.id,
                    name = name,
                    imageUrl = response.cover?.url?.let { url ->
                        "https:$url".replace("t_thumb", "t_cover_big")
                    },
                    releaseYear = year,
                    publisher = publisher,
                    genre = genre
                )
            }
            .firstOrNull()
    }
}