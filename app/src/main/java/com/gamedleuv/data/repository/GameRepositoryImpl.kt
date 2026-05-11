package com.gamedleuv.data.repository

import android.util.Log
import com.gamedleuv.data.remote.api.IgdbApiService
import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.repository.GameRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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
                    name = it.name,
                    imageUrl = it.cover?.url?.let { url ->
                        "https:$url".replace("t_thumb", "t_cover_big")
                    }
                )
            }
    }

    override suspend fun getRandomGame(): Game? {
        val offset = (0..3000).random() //importante, puede devolver nulos si el valor es alto
        val bodyString = """
        fields name, cover.url;
        where cover != null & rating_count > 30;
        limit 1;
        offset $offset;
    """.trimIndent()

        val body = bodyString.toRequestBody("text/plain".toMediaType())

        return api.searchGames(body = body)
            .mapNotNull {
                val name = it.name ?: return@mapNotNull null
                Game(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.cover?.url?.let { url ->
                        "https:$url".replace("t_thumb", "t_cover_big")
                    }
                )
            }
            .firstOrNull()
    }
}
