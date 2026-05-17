package com.gamedleuv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamedleuv.domain.repository.RoomRepository
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase

class RoomViewModelFactory(
    private val roomRepository: RoomRepository,
    private val searchGamesUseCase: SearchGamesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RoomViewModel(roomRepository, searchGamesUseCase) as T
    }
}