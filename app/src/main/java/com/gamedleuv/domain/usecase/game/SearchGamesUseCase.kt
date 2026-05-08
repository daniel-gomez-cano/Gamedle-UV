package com.gamedleuv.domain.usecase.game

import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.repository.GameRepository

class SearchGamesUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(query: String): List<Game> {
        return repository.searchGames(query)
    }
}