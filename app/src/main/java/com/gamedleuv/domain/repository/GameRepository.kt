package com.gamedleuv.domain.repository

import com.gamedleuv.domain.model.Game

interface GameRepository {
    suspend fun searchGames(query: String): List<Game>
    suspend fun getRandomGame(): Game?
}
