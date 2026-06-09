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
    val hintUnlocked: Boolean = true,   // La pista empieza desbloqueada
    val hintUsed: Boolean = false,
    val hintGamesLeft: Int = 0,         // Juegos restantes para recargar la pista (0 = disponible)
    val currentHint: GameHint? = null,
    val isGameOver: Boolean = false,
    val isRevealingAnswer: Boolean = false,
    val isProcessingGuess: Boolean = false
)

data class GameHint(
    val releaseYear: String,
    val publisher: String,
    val genre: String,
    val hangmanDisplay: String      // Texto tipo ahorcado precalculado, listo para mostrar
)

class GameViewModel(
    private val searchGamesUseCase: SearchGamesUseCase,
    private val getRandomGameUseCase: GetRandomGameUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        // Número de juegos sin usar la pista necesarios para recargarla
        // EDITAR EN CASO DE CAMBIAR EL NÚMERO DE JUEGOS NECESARIOS PARA RECARGARLA
        private const val HINT_RECHARGE_GAMES = 5
    }

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    private var currentGame: Game? = null
    private var consecutiveWins: Int = 0

    // Cuántos juegos ha jugado el usuario con la pista desactivada (sin usarla)
    // Cuando llega a HINT_RECHARGE_GAMES la pista se reactiva
    private var gamesWithoutHint: Int = 0

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

    private fun loadRandomGame(isFirstLoad: Boolean = false) {
        viewModelScope.launch {
            val initialSector = (0..8).random()


            val newHintUnlocked: Boolean
            val newGamesWithoutHint: Int

            if (isFirstLoad) {
                newHintUnlocked = true
                newGamesWithoutHint = 0
            } else {
                val hintWasUsed = _uiState.value.hintUsed

                if (_uiState.value.hintUnlocked && !hintWasUsed) {
                    // La pista estaba disponible pero el usuario NO la usó.
                    // Sigue desbloqueada para el siguiente juego.
                    newHintUnlocked = true
                    newGamesWithoutHint = gamesWithoutHint
                } else if (hintWasUsed) {
                    // El usuario usó la pista -> desactivarla y empezar a contar.
                    newGamesWithoutHint = 1
                    newHintUnlocked = false
                } else {
                    // Pista ya estaba desactivada -> incrementar contador.
                    val updated = gamesWithoutHint + 1
                    newGamesWithoutHint = if (updated >= HINT_RECHARGE_GAMES) 0 else updated
                    newHintUnlocked = updated >= HINT_RECHARGE_GAMES
                }
            }

            gamesWithoutHint = newGamesWithoutHint

            _uiState.value = _uiState.value.copy(
                searchQuery = "",
                selectedGame = "",
                gameList = emptyList(),
                revealedSectors = listOf(initialSector),
                hintUsed = false,
                hintUnlocked = newHintUnlocked,
                hintGamesLeft = if (newHintUnlocked) 0 else HINT_RECHARGE_GAMES - newGamesWithoutHint,
                currentHint = null
            )
            try {
                val nextGame = getRandomGameUseCase()
                Log.d("GAME_DEBUG", "Game seleccionado: ${nextGame?.name}")
                Log.d("GAME_DEBUG", "URL imagen: ${nextGame?.imageUrl}")
                currentGame = nextGame
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
        if (_uiState.value.isProcessingGuess) return // <- bloquea spam
        _uiState.value = _uiState.value.copy(isProcessingGuess = true)

        val currentState = _uiState.value
        val isCorrect = currentState.searchQuery.trim()
            .equals(currentGame?.name?.trim(), ignoreCase = true)
        if (isCorrect) handleCorrectGuess() else {
            showSector()
            _uiState.value = _uiState.value.copy(isProcessingGuess = false)
        }
    }

    private fun handleCorrectGuess() {
        consecutiveWins++
        val newStreak = _uiState.value.streak + 1

        _uiState.value = _uiState.value.copy(
            revealedSectors = (0..8).toList(),
            lives = _uiState.value.maxLives,
            streak = newStreak,
            isProcessingGuess = true // mantiene bloqueado durante el delay
        )
        viewModelScope.launch {
            delay(1400)
            _uiState.value = _uiState.value.copy(isProcessingGuess = false)
            loadRandomGame()
        }
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

        if (isGameOver) {
            consecutiveWins = 0
            val finalStreak = _uiState.value.streak

            // Revela toda la imagen antes del game over
            _uiState.value = _uiState.value.copy(
                lives = newLives,
                revealedSectors = (0..8).toList(), // ← toda la imagen
                isRevealingAnswer = true
            )

            viewModelScope.launch {
                val uid = authRepository.getCurrentUser()?.id
                if (uid != null) {
                    val result = authRepository.updateStreakIfHigher(uid, finalStreak)
                    result.onSuccess { Log.d("STREAK_DEBUG", "Racha guardada: $finalStreak") }
                    result.onFailure { Log.e("STREAK_DEBUG", "Error guardando racha: ${it.message}") }
                } else {
                    Log.e("STREAK_DEBUG", "uid nulo, no se guardó la racha")
                }

                delay(3000) // ← 3 segundos mostrando la imagen completa
                _uiState.value = _uiState.value.copy(
                    isRevealingAnswer = false,
                    isGameOver = true
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(lives = newLives)
        }
    }

    // ─── Pista ───────────────────────────────────────────────────────────────

    fun onUseHint() {
        val state = _uiState.value
        if (!state.hintUnlocked || state.hintUsed) return
        val hint = buildHint()
        _uiState.value = state.copy(
            currentHint = hint,
            hintUsed = true
        )
    }

    fun onDismissHint() {
        _uiState.value = _uiState.value.copy(currentHint = null)
    }

    fun onReopenHint() {
        val state = _uiState.value
        if (!state.hintUsed) return
        val hint = buildHint()
        _uiState.value = state.copy(currentHint = hint)
    }

    // ─── Game over / reset ───────────────────────────────────────────────────

    fun onDismissGameOver() {
        consecutiveWins = 0
        gamesWithoutHint = 0
        _uiState.value = GameUiState()
        loadRandomGame(isFirstLoad = true)
    }

    fun resetGame() {
        consecutiveWins = 0
        gamesWithoutHint = 0
        _uiState.value = GameUiState()
        loadRandomGame(isFirstLoad = true)
    }

    // ─── Construcción de pista ───────────────────────────────────────────────

    /** Construye el GameHint con el display de ahorcado ya calculado. */
    private fun buildHint(): GameHint = GameHint(
        releaseYear    = currentGame?.releaseYear ?: "Desconocido",
        publisher      = currentGame?.publisher   ?: "Desconocido",
        genre          = currentGame?.genre       ?: "Desconocido",
        hangmanDisplay = buildHangmanDisplay(currentGame?.name ?: "")
    )

    /**
     * Genera el texto tipo ahorcado para el nombre del juego.
     *
     * Reglas:
     * - Se revelan siempre las 2 primeras y 2 últimas letras no-espacio.
     * - Los espacios se conservan como separadores.
     * - Nombres largos (>30 chars y >4 palabras): se muestran las 2 primeras
     *   y 2 últimas palabras separadas por " ... ".
     *
     * Ejemplos:
     *   "Super Mario Bros"                  → "Su___ _____ __os"
     *   "The Legend of Zelda"               → "Th_ ______ __ ___da"
     *   "Tom Clancy's Rainbow Six Siege"    → "To_ _______'_ ... ___ ____ge"
     */
    private fun buildHangmanDisplay(name: String): String {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return ""

        // Índices de caracteres no-espacio
        val nonSpaceIndices = trimmed.indices.filter { trimmed[it] != ' ' }

        // Conjunto de índices a revelar: 2 primeros y 2 últimos caracteres no-espacio
        val revealIndices = buildSet {
            if (nonSpaceIndices.size >= 1) add(nonSpaceIndices[0])
            if (nonSpaceIndices.size >= 2) add(nonSpaceIndices[1])
            if (nonSpaceIndices.size >= 3) add(nonSpaceIndices[nonSpaceIndices.size - 1])
            if (nonSpaceIndices.size >= 4) add(nonSpaceIndices[nonSpaceIndices.size - 2])
        }

        // Máscara completa: letras reveladas, resto como '_', espacios conservados
        val fullyMasked = trimmed.mapIndexed { i, c ->
            when {
                c == ' '        -> ' '
                i in revealIndices -> c
                else            -> '_'
            }
        }.joinToString("")

        val words = trimmed.split(" ")
        val isLong = trimmed.length > 30 && words.size > 4
        if (!isLong) return fullyMasked

        val firstPart = words.take(2).joinToString(" ")
        val lastPart  = words.takeLast(2).joinToString(" ")

        // Si las dos partes solapan o casi solapan, mostrar todo completo
        if (firstPart.length + lastPart.length + 5 >= trimmed.length) return fullyMasked

        val maskedFirst = fullyMasked.substring(0, firstPart.length)
        val lastPartStart = trimmed.length - lastPart.length
        val maskedLast  = fullyMasked.substring(lastPartStart)

        return "$maskedFirst ... $maskedLast"
    }
}