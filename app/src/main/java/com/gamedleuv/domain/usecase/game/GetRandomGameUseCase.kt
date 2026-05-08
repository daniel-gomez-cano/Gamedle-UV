package com.gamedleuv.domain.usecase.game

import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.repository.GameRepository

class GetRandomGameUseCase(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): Game? {
        return repository.getRandomGame()
    }
}