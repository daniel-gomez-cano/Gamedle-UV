package com.gamedleuv.ui.viewmodel

import com.gamedleuv.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val gameImage: Int = R.drawable.ic_launcher_background, // Imagen temporal hasta tener API
    val lives: Int = 5,
    val maxLives: Int = 5,
    val gameList: List<String> = emptyList(),
    val selectedGame: String = "",
    val isLoading: Boolean = false
)

class GameViewModel(
    private val scope: CoroutineScope
) {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    // Lista hardcodeada temporal, reemplazar cuando haya API
    private val mockGameList = listOf(
        "A Dance of Fire and Ice",
        "A Difficult Game About Climbing",
        "A Game About Digging A Hole",
        "A Story About My Uncle",
        "Abiotic Factor",
        "Among Us",
        "Apex Legends",
        "Baldur's Gate 3",
        "Celeste",
        "Cuphead",
        "Dead Cells",
        "Disco Elysium",
        "Elden Ring",
        "Hades",
        "Hollow Knight",
        "It Takes Two",
        "Minecraft",
        "Portal 2",
        "Stardew Valley",
        "The Witcher 3"
    )

    init {
        loadGameData()
    }

    private fun loadGameData() {
        scope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // TODO: Reemplazar con llamada real a la API cuando esté disponible
            // val result = getRandomGameUseCase()

            _uiState.value = _uiState.value.copy(
                gameImage = R.drawable.ic_launcher_background,
                gameList = mockGameList,
                isLoading = false
            )
        }
    }

    fun onGameSelected(game: String) {
        _uiState.value = _uiState.value.copy(selectedGame = game)
    }

    fun onSkip() {
        //TODO: Implementar la logica del blur para el juego
        loseLife()
    }

    fun onGuess() {
        // TODO: Implementar lógica de adivinanza contra la API
        // Por ahora solo consume una vida como placeholder
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
        loadGameData()
    }
}
