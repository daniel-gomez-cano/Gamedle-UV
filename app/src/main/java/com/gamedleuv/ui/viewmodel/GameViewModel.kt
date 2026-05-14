package com.gamedleuv.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.repository.AuthRepository
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
    val revealedSectors: List<Int> = emptyList(),
    val streak: Int = 0,
    val hintUnlocked: Boolean = true,
    val hintUsed: Boolean = true,
    val currentHint: GameHint? = null,
    val isGameOver: Boolean = false
)

data class GameHint(
    val releaseYear: String,
    val publisher: String,
    val genre: String
)

class GameViewModel(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val getRandomGameUseCase: GetRandomGameUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private var currentGame: Game? = null
    private var consecutiveWins: Int = 0

    init {
        loadRandomGame()
    }

    // ─── Búsqueda con debounce ───────────────────────────────────────────────

    private var searchJob: Job? = null

    fun searchGames(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(gameList = emptyList())
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
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

    // ─── Carga de juego ─────────────────────────────────────────────────────

    private fun loadRandomGame() {
        viewModelScope.launch {
            val initialSector = (0..8).random()
            _uiState.value = _uiState.value.copy(
                gameImageUrl = null,
                isLoading = true,
                searchQuery = "",
                selectedGame = "",
                gameList = emptyList(),
                revealedSectors = listOf(initialSector),
                hintUsed = false,
                currentHint = null
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

    // ─── Selección ──────────────────────────────────────────────────────────

    fun onGameSelected(game: String) {
        _uiState.value = _uiState.value.copy(
            selectedGame = game,
            searchQuery = game
        )
    }

    // ─── Adivinanza ─────────────────────────────────────────────────────────

    fun onGuess() {
        val currentState = _uiState.value
        val isCorrect = currentState.searchQuery.trim()
            .equals(currentGame?.name?.trim(), ignoreCase = true)
        if (isCorrect) handleCorrectGuess() else showSector()
    }

    private fun handleCorrectGuess() {
        consecutiveWins++
        val newStreak = _uiState.value.streak + 1
        val newHintUnlocked = consecutiveWins % 10 == 0

        _uiState.value = _uiState.value.copy(
            revealedSectors = (0..8).toList(),
            lives = _uiState.value.maxLives,
            streak = newStreak,
            hintUnlocked = newHintUnlocked
        )
        loadRandomGame()
    }

    fun onSkip() { showSector() }

    // ─── Sector + vida ───────────────────────────────────────────────────────

    fun showSector() {
        val currentState = _uiState.value
        val hiddenSectors = (0..8).filter { it !in currentState.revealedSectors }
        val randomSector = hiddenSectors.randomOrNull()
        val updatedSectors = if (randomSector != null)
            currentState.revealedSectors + randomSector
        else
            currentState.revealedSectors

        _uiState.value = currentState.copy(revealedSectors = updatedSectors)
        loseLife()
    }

    private fun loseLife() {
        val current = _uiState.value.lives
        val newLives = if (current > 0) current - 1 else 0
        val isGameOver = newLives == 0

        _uiState.value = _uiState.value.copy(
            lives = newLives,
            isGameOver = isGameOver
        )

        if (isGameOver) {
            consecutiveWins = 0
            val finalStreak = _uiState.value.streak

            // ← FIX: obtener el uid en el momento de guardar, no al construir el ViewModel.
            // getCurrentUser() lee de FirebaseAuth que ya tiene sesión activa,
            // así el uid nunca es "".
            val uid = authRepository.getCurrentUser()?.id
            if (uid != null) {
                viewModelScope.launch {
                    val result = authRepository.updateStreakIfHigher(uid, finalStreak)
                    result.onSuccess {
                        Log.d("STREAK_DEBUG", "Racha guardada: $finalStreak para uid: $uid")
                    }
                    result.onFailure {
                        Log.e("STREAK_DEBUG", "Error guardando racha: ${it.message}")
                    }
                }
            } else {
                Log.e("STREAK_DEBUG", "uid nulo, no se guardó la racha")
            }
        }
    }

    // ─── Pista ───────────────────────────────────────────────────────────────

    fun onUseHint() {
        val state = _uiState.value
        if (!state.hintUnlocked || state.hintUsed) return
        val hint = GameHint(
            releaseYear = currentGame?.releaseYear ?: "Desconocido",
            publisher   = currentGame?.publisher   ?: "Desconocido",
            genre       = currentGame?.genre       ?: "Desconocido"
        )
        _uiState.value = state.copy(
            currentHint = hint,
            hintUsed = true,
            hintUnlocked = false
        )
    }

    fun onDismissHint() {
        _uiState.value = _uiState.value.copy(currentHint = null)
    }

    fun onReopenHint() {
        val state = _uiState.value
        if (!state.hintUsed) return
        val hint = GameHint(
            releaseYear = currentGame?.releaseYear ?: "Desconocido",
            publisher   = currentGame?.publisher   ?: "Desconocido",
            genre       = currentGame?.genre       ?: "Desconocido"
        )
        _uiState.value = state.copy(currentHint = hint)
    }

    // ─── Game over / reset ───────────────────────────────────────────────────

    fun onDismissGameOver() {
        consecutiveWins = 0
        _uiState.value = GameUiState()
        loadRandomGame()
    }

    fun resetGame() {
        consecutiveWins = 0
        _uiState.value = GameUiState()
        loadRandomGame()
    }
}