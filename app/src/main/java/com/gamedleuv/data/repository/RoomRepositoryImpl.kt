package com.gamedleuv.data.repository

import com.gamedleuv.data.remote.pvp.model.RoomState
import com.gamedleuv.data.remote.pvp.model.PlayerState
import com.gamedleuv.domain.repository.GameRepository
import com.gamedleuv.domain.repository.RoomRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepositoryImpl(
    private val db: FirebaseDatabase,
    private val gameRepository: GameRepository
) : RoomRepository {

    private val rooms = db.getReference("rooms")

    // Genera código de 4 letras aleatorio
    private fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..4).map { chars.random() }.joinToString("")
    }

    override suspend fun createRoom(uid: String, username: String): String {
        val code = generateCode()

        // Obtiene un juego aleatorio de IGDB para esta sala
        val game = gameRepository.getRandomGame()

        val room = RoomState(
            gameImageUrl = game?.imageUrl ?: "",
            gameName = game?.name ?: "",
            status = "waiting",
            players = mapOf(
                "player1" to PlayerState(
                    uid = uid,
                    username = username,
                    lives = 5
                )
            )
        )

        rooms.child(code).setValue(room).await()
        return code
    }

    override suspend fun joinRoom(code: String, uid: String, username: String): Boolean {
        val snapshot = rooms.child(code).get().await()
        if (!snapshot.exists()) return false

        val room = snapshot.getValue(RoomState::class.java) ?: return false
        if (room.status != "waiting") return false
        if (room.players.size >= 2) return false

        // Agrega player2 y cambia status a playing
        rooms.child(code).child("players").child("player2").setValue(
            PlayerState(uid = uid, username = username, lives = 5)
        ).await()

        rooms.child(code).child("status").setValue("playing").await()
        return true
    }

    override fun observeRoom(code: String): Flow<RoomState?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(RoomState::class.java)
                trySend(room)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        rooms.child(code).addValueEventListener(listener)
        awaitClose { rooms.child(code).removeEventListener(listener) }
    }

    override suspend fun submitGuess(
        code: String,
        uid: String,
        guess: String,
        gameName: String
    ) {
        val snapshot = rooms.child(code).get().await()
        val room = snapshot.getValue(RoomState::class.java) ?: return

        // Identifica qué player es el que adivina
        val playerKey = room.players.entries
            .firstOrNull { it.value.uid == uid }?.key ?: return

        val rivalKey = if (playerKey == "player1") "player2" else "player1"
        val isCorrect = guess.trim().equals(gameName.trim(), ignoreCase = true)

        if (isCorrect) {
            // Le quita una vida al rival
            val rivalLives = (room.players[rivalKey]?.lives ?: 0) - 1
            rooms.child(code).child("players").child(rivalKey)
                .child("lives").setValue(rivalLives).await()

            if (rivalLives <= 0) {
                rooms.child(code).child("status").setValue("finished").await()
            } else {
                // Carga el siguiente juego
                val nextGame = gameRepository.getRandomGame()
                rooms.child(code).updateChildren(
                    mapOf(
                        "gameImageUrl" to (nextGame?.imageUrl ?: ""),
                        "gameName" to (nextGame?.name ?: ""),
                        "currentRound" to (room.currentRound + 1),
                        "players/player1/hasGuessed" to false,
                        "players/player2/hasGuessed" to false,
                        "players/player1/lastGuess" to "",
                        "players/player2/lastGuess" to ""
                    )
                ).await()
            }
        } else {
            // Respuesta incorrecta: le quita una vida al que se equivocó
            val myLives = (room.players[playerKey]?.lives ?: 0) - 1
            rooms.child(code).child("players").child(playerKey)
                .child("lives").setValue(myLives).await()

            rooms.child(code).child("players").child(playerKey)
                .child("hasGuessed").setValue(true).await()

            if (myLives <= 0) {
                rooms.child(code).child("status").setValue("finished").await()
            }
        }
    }

    override suspend fun skipTurn(code: String, uid: String) {
        val snapshot = rooms.child(code).get().await()
        val room = snapshot.getValue(RoomState::class.java) ?: return
        val playerKey = room.players.entries
            .firstOrNull { it.value.uid == uid }?.key ?: return

        val myLives = (room.players[playerKey]?.lives ?: 0) - 1
        rooms.child(code).child("players").child(playerKey)
            .child("lives").setValue(myLives).await()

        if (myLives <= 0) {
            rooms.child(code).child("status").setValue("finished").await()
        }
    }
}