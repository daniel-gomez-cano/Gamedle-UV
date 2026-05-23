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

    override suspend fun createRoom(uid: String, username: String, profilePictureUrl: String): String {
        val code = generateCode()

        // Obtiene un juego aleatorio de IGDB para esta sala
        val game = gameRepository.getRandomGame()

        val room = RoomState(
            gameImageUrl = game?.imageUrl ?: "",
            gameName = game?.name ?: "",
            status = "waiting",
            roundEndTime = System.currentTimeMillis() + 30000,
            players = mapOf(
                "player1" to PlayerState(
                    uid = uid,
                    username = username,
                    lives = 5,
                    profilePictureUrl = profilePictureUrl
                )
            )
        )

        rooms.child(code).setValue(room).await()
        return code
    }

    override suspend fun joinRoom(code: String, uid: String, username: String, profilePictureUrl: String): Boolean {
        val snapshot = rooms.child(code).get().await()
        if (!snapshot.exists()) return false

        val room = snapshot.getValue(RoomState::class.java) ?: return false
        if (room.status != "waiting") return false
        if (room.players.size >= 2) return false

        // Agrega player2 y cambia status a playing
        rooms.child(code).child("players").child("player2").setValue(
            PlayerState(uid = uid, username = username, lives = 5, profilePictureUrl = profilePictureUrl)
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

    override suspend fun submitGuess(code: String, uid: String, guess: String, gameName: String) {

        val snapshot = rooms.child(code).get().await()
        val room = snapshot.getValue(RoomState::class.java) ?: return

        val playerKey = room.players.entries
            .firstOrNull { it.value.uid == uid }?.key ?: return

        val isCorrect =
            guess.trim().equals(gameName.trim(), ignoreCase = true)

        val responseTime =
            System.currentTimeMillis() - room.roundEndTime

        rooms.child(code)
            .child("players")
            .child(playerKey)
            .updateChildren(
                mapOf(
                    "hasAnswered" to true,
                    "answeredCorrectly" to isCorrect,
                    "responseTime" to responseTime
                )
            ).await()

        evaluateRound(code) //Guarda la respuesta de cada jugador en firebase
    }

    private suspend fun evaluateRound(code: String) {
        val snapshot = rooms.child(code).get().await()
        val room = snapshot.getValue(RoomState::class.java) ?: return
        val p1 = room.players["player1"] ?: return
        val p2 = room.players["player2"] ?: return

        // Esperar a que ambos respondan
        if (!p1.hasAnswered || !p2.hasAnswered) return

        // Solo el primer cliente que escriba "evaluating" gana la race condition
        val statusRef = rooms.child(code).child("status")
        val currentStatus = statusRef.get().await().getValue(String::class.java)

        // Si ya alguien está evaluando o terminó, salir
        if (currentStatus == "evaluating" || currentStatus == "finished") return

        // Intentar tomar que el que llegue primero escribe "evaluating"
        statusRef.setValue("evaluating").await()

        val verifyStatus = statusRef.get().await().getValue(String::class.java)
        if (verifyStatus != "evaluating") return

        var p1Lives = p1.lives
        var p2Lives = p2.lives

        when { // Evaluamos las condiciones de ganar, quien responde antes, si uno se equivoca o si ninguno responde
            p1.answeredCorrectly && p2.answeredCorrectly -> {
                if (p1.responseTime > p2.responseTime) p1Lives-- else p2Lives--
            }
            p1.answeredCorrectly && !p2.answeredCorrectly -> {
                p2Lives--
            }
            !p1.answeredCorrectly && p2.answeredCorrectly -> {
                p1Lives--
            }
            else -> {
                p1Lives--
                p2Lives--
            }
        }

        rooms.child(code).updateChildren(
            mapOf(
                "players/player1/lives" to p1Lives,
                "players/player2/lives" to p2Lives
            )
        ).await()

        if (p1Lives <= 0 || p2Lives <= 0) {
            rooms.child(code).child("status").setValue("finished").await()
            return
        }

        nextRound(code, room)
    }


    private suspend fun nextRound(code: String, room: RoomState) { // para la siguiente ronda cambia de juego y restaura el timer
        val nextGame = gameRepository.getRandomGame()

        rooms.child(code).updateChildren(
            mapOf(
                "gameImageUrl" to (nextGame?.imageUrl ?: ""),
                "gameName" to (nextGame?.name ?: ""),
                "currentRound" to (room.currentRound + 1),
                "roundEndTime" to System.currentTimeMillis() + 30000,
                "status" to "playing",

                "players/player1/hasAnswered" to false,
                "players/player2/hasAnswered" to false,
                "players/player1/answeredCorrectly" to false,
                "players/player2/answeredCorrectly" to false,
                "players/player1/responseTime" to 0L,
                "players/player2/responseTime" to 0L
            )
        ).await()
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

    override suspend fun evaluateTimer(code: String) { // Evalua el tiempo dentro del juego, toma el tiempo de respuesta de cada jugador y si nadie respondio en los 30 segundos pierden ambos
        val snapshot = rooms.child(code).get().await()
        val room = snapshot.getValue(RoomState::class.java) ?: return

        val updates = mutableMapOf<String, Any>()

        room.players.forEach { (key, player) ->
            if (!player.hasAnswered) {
                updates["players/$key/hasAnswered"] = true
                updates["players/$key/answeredCorrectly"] = false
                updates["players/$key/responseTime"] = Long.MAX_VALUE
            }
        }

        if (updates.isNotEmpty()) {
            rooms.child(code).updateChildren(updates).await()
        }
        evaluateRound(code)
    }
}