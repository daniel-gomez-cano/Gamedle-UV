package com.gamedleuv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamedleuv.data.remote.pvp.model.RoomState
import com.gamedleuv.domain.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RoomUiState(
    val roomCode: String = "",
    val room: RoomState? = null,
    val myUid: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val gameList: List<String> = emptyList()
)

class RoomViewModel(
    private val roomRepository: RoomRepository,
    private val searchGamesUseCase: com.gamedleuv.domain.usecase.game.SearchGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState

    private var searchJob: kotlinx.coroutines.Job? = null

    fun createRoom(uid: String, username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val code = roomRepository.createRoom(uid, username)
                _uiState.value = _uiState.value.copy(
                    roomCode = code,
                    myUid = uid,
                    isLoading = false
                )
                observeRoom(code)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error creando sala: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun joinRoom(code: String, uid: String, username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val joined = roomRepository.joinRoom(code, uid, username)
                if (joined) {
                    _uiState.value = _uiState.value.copy(
                        roomCode = code,
                        myUid = uid,
                        isLoading = false
                    )
                    observeRoom(code)
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Sala no encontrada o ya está llena",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error uniéndose: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun observeRoom(code: String) {
        viewModelScope.launch {
            roomRepository.observeRoom(code).collect { room ->
                _uiState.value = _uiState.value.copy(room = room)
            }
        }
    }

    fun resetRoom() {
        _uiState.value = RoomUiState()
    }

    fun searchGames(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(gameList = emptyList())
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            try {
                val result = searchGamesUseCase(query)
                _uiState.value = _uiState.value.copy(
                    gameList = result.map { it.name }.distinct()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onGameSelected(game: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = game,
            gameList = emptyList()
        )
    }

    fun onGuess() {
        val state = _uiState.value
        val code = state.roomCode
        val uid = state.myUid
        val guess = state.searchQuery
        val gameName = state.room?.gameName ?: return

        viewModelScope.launch {
            try {
                roomRepository.submitGuess(code, uid, guess, gameName)
                _uiState.value = _uiState.value.copy(
                    searchQuery = "",
                    gameList = emptyList()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onSkip() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                roomRepository.skipTurn(state.roomCode, state.myUid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}