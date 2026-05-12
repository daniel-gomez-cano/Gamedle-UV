package com.gamedleuv.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.usecase.game.GetRandomGameUseCase
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val gameImageUrl: String? = null,
    val lives: Int = 5,
    val maxLives: Int = 5,
    val gameList: List<String> = emptyList(),
    val selectedGame: String = "",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val revealedSectors: List<Int> = emptyList()
)

class GameViewModel(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val getRandomGameUseCase: GetRandomGameUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private var currentGame: Game? = null

    init {
        loadRandomGame()
    }

    private var searchJob: Job? = null
    fun searchGames(query: String) {
        // 1. Actualiza el estado con lo que el usuario escribe
        _uiState.value = _uiState.value.copy(searchQuery = query)
        // 2. Si lo que el mansito escribe es muy corto, limpiamos la lista y salimos
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(gameList = emptyList())
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
        // 3. Si pasa la validación, procede con la búsqueda
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
    private fun loadRandomGame() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                gameImageUrl = currentGame?.imageUrl,
                isLoading = false,
                searchQuery = "",       // ← limpia el input
                selectedGame = "",      // ← limpia la selección
                gameList = emptyList()
            )
            try {
                currentGame = getRandomGameUseCase()
                Log.d("GAME_DEBUG", "Game seleccionado: ${currentGame?.name}")
                Log.d("GAME_DEBUG", "URL imagen: ${currentGame?.imageUrl}")
                _uiState.value = _uiState.value.copy(
                    gameImageUrl = currentGame?.imageUrl,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("GAME_DEBUG", "Error cargando juego: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    fun onGameSelected(game: String) {
        _uiState.value = _uiState.value.copy(
            selectedGame = game,
            searchQuery = game // sincroniza el fkn input
        )
    }

    fun onGuess() {
        val isCorrect = _uiState.value.selectedGame.equals(
            currentGame?.name, ignoreCase = true
        )
        if (isCorrect) {
            loadRandomGame()
        } else {
            loseLife()
        }
    }
    fun onSkip() {
        //TODO: Implementar la logica del blur para el juego
        loseLife()
    }

    private fun loseLife() {
        val current = _uiState.value.lives
        if (current > 0) {
            _uiState.value = _uiState.value.copy(lives = current - 1)
        }
    }

    fun resetGame() {
        _uiState.value = GameUiState()
        loadRandomGame()
    }
}
