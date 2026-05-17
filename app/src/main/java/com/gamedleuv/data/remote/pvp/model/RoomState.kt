package com.gamedleuv.data.remote.pvp.model

data class RoomState(
    val gameImageUrl: String = "",
    val gameName: String = "",
    val status: String = "waiting", // waiting | playing | finished
    val currentRound: Int = 1,
    val players: Map<String, PlayerState> = emptyMap()
)

data class PlayerState(
    val uid: String = "",
    val username: String = "",
    val lives: Int = 5,
    val hasGuessed: Boolean = false,
    val lastGuess: String = ""
)