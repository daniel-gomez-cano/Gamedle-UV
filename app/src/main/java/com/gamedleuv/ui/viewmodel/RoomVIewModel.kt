package com.gamedleuv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamedleuv.data.remote.pvp.model.RoomState
import com.gamedleuv.domain.repository.RoomRepository
import com.gamedleuv.ui.viewmodel.enums.PvpGameResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val gameList: List<String> = emptyList(),

    //TODO: Crear la logica del contador
    val remainingSeconds: Int = 30,
    val gameResult: PvpGameResult? = null,
    val showResultDialog: Boolean = false
)

class RoomViewModel(
    private val roomRepository: RoomRepository,
    private val searchGamesUseCase: com.gamedleuv.domain.usecase.game.SearchGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState

    private var searchJob: Job? = null

    private var timerJob: Job? = null

    fun createRoom(uid: String, username: String, profilePictureUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val code = roomRepository.createRoom(uid, username, profilePictureUrl)
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

    fun joinRoom(code: String, uid: String, username: String, profilePictureUrl: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val joined = roomRepository.joinRoom(code, uid, username, profilePictureUrl)
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

    private var lastRound = -1
    private var lastStatus = ""
    private var lastRoundEndTime = 0L
    private fun observeRoom(code: String) {
        viewModelScope.launch {
            roomRepository.observeRoom(code).collect { room ->
                _uiState.value = _uiState.value.copy(room = room)

                if (room == null) return@collect

                if (room.currentRound != lastRound) {
                    lastRound = room.currentRound
                }

                if (room.roundEndTime != lastRoundEndTime) {
                    lastRoundEndTime = room.roundEndTime
                    startTimer()
                }

                if (room.status != lastStatus) {
                    lastStatus = room.status
                    when (room.status) {
                        "finished" -> {
                            timerJob?.cancel()
                            resolveGameResult(room)
                        }
                    }
                }
            }
        }
    }

    private fun startTimer() {

        timerJob?.cancel()

        val roomEndTime = _uiState.value.room?.roundEndTime ?: return

        timerJob = viewModelScope.launch {

            while (true) {
                val room = _uiState.value.room

                if (room != null) {
                    val remaining =
                        ((room.roundEndTime - System.currentTimeMillis()) / 1000)
                            .toInt()
                            .coerceAtLeast(0)
                    _uiState.value =
                        _uiState.value.copy(
                            remainingSeconds = remaining
                        )
                    if (remaining <= 0) {
                        val room = _uiState.value.room
                        val myUid = _uiState.value.myUid
                        val isPlayer1 = room?.players?.get("player1")?.uid == myUid

                        if (isPlayer1) {
                            roomRepository.evaluateTimer(_uiState.value.roomCode)
                        }
                        break
                    }
                }

                delay(1000)
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
                roomRepository.revealNextSector(state.roomCode) //
                roomRepository.skipTurn(state.roomCode, state.myUid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    //Logica basica para determinar el ganador de la partida
    //TODO: Mejorar lógica para determinar ganador e implementarla
    fun onDismissResult() {
        _uiState.value = _uiState.value.copy(showResultDialog = false)
    }

    private fun resolveGameResult(room: RoomState) {
        val myUid   = _uiState.value.myUid
        val players = room.players
        val me      = players.values.firstOrNull { it.uid == myUid }
        val rival   = players.values.firstOrNull { it.uid != myUid }

        val result = when {
            (me?.lives ?: 0) > (rival?.lives ?: 0) -> PvpGameResult.WIN
            (me?.lives ?: 0) < (rival?.lives ?: 0) -> PvpGameResult.LOSE
            else                                    -> PvpGameResult.DRAW //Valor por defecto en caso de fallo :,v
        }

        _uiState.value = _uiState.value.copy(
            gameResult = result,
            showResultDialog = true
        )
    }
}