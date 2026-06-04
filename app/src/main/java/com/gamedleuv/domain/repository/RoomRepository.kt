package com.gamedleuv.domain.repository

import com.gamedleuv.data.remote.pvp.model.RoomState
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun createRoom(uid: String, username: String, profilePictureUrl: String): String  // devuelve el código
    suspend fun joinRoom(code: String, uid: String, username: String, profilePictureUrl: String): Boolean
    fun observeRoom(code: String): Flow<RoomState?>
    suspend fun submitGuess(code: String, uid: String, guess: String, gameName: String)
    suspend fun skipTurn(code: String, uid: String)
    suspend fun evaluateTimer(code: String)

    suspend fun revealNextSector(code: String)
}