package com.gamedleuv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamedleuv.domain.repository.AuthRepository
import com.gamedleuv.domain.usecase.game.GetRandomGameUseCase
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase

class GameViewModelFactory(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val getRandomGameUseCase: GetRandomGameUseCase,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(
            searchGamesUseCase,
            getRandomGameUseCase,
            authRepository
        ) as T
    }
}