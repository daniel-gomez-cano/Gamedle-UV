package com.gamedleuv

import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.model.GameHint
import com.gamedleuv.domain.model.User
import org.junit.Assert.*
import org.junit.Test

// ─────────────────────────────────────────────────────────────────────────────
// Tests del modelo Game
// ─────────────────────────────────────────────────────────────────────────────

class GameModelTest {

    @Test
    fun game_seCreaCorrectamenteConTodosLosCampos() {
        val game = Game(
            id = 1,
            name = "The Legend of Zelda",
            imageUrl = "https://images.igdb.com/zelda.jpg",
            releaseYear = "1986",
            publisher = "Nintendo",
            genre = "Adventure"
        )

        assertEquals(1, game.id)
        assertEquals("The Legend of Zelda", game.name)
        assertEquals("https://images.igdb.com/zelda.jpg", game.imageUrl)
        assertEquals("1986", game.releaseYear)
        assertEquals("Nintendo", game.publisher)
        assertEquals("Adventure", game.genre)
    }

    @Test
    fun game_aceptaCamposNullablesComoNull() {
        val game = Game(
            id = 2,
            name = "Juego Sin Datos",
            imageUrl = null,
            releaseYear = null,
            publisher = null,
            genre = null
        )

        assertNull(game.imageUrl)
        assertNull(game.releaseYear)
        assertNull(game.publisher)
        assertNull(game.genre)
    }

    @Test
    fun game_dosObjetosConMismosDatossonIguales() {
        val game1 = Game(1, "Minecraft", null, "2011", "Mojang", "Sandbox")
        val game2 = Game(1, "Minecraft", null, "2011", "Mojang", "Sandbox")

        assertEquals(game1, game2)
    }

    @Test
    fun game_dosObjetosConDistintoIdSonDiferentes() {
        val game1 = Game(1, "Minecraft", null, "2011", "Mojang", "Sandbox")
        val game2 = Game(2, "Minecraft", null, "2011", "Mojang", "Sandbox")

        assertNotEquals(game1, game2)
    }

    @Test
    fun game_copySoloModificaElCampoIndicado() {
        val original = Game(1, "Original", null, "2000", "Dev", "RPG")
        val copia = original.copy(name = "Copia")

        assertEquals("Copia", copia.name)
        assertEquals(original.id, copia.id)
        assertEquals(original.releaseYear, copia.releaseYear)
        assertEquals(original.publisher, copia.publisher)
        assertEquals(original.genre, copia.genre)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests del modelo User
// ─────────────────────────────────────────────────────────────────────────────

class UserModelTest {

    @Test
    fun user_seCreaCorrectamenteConTodosLosCampos() {
        val user = User(
            id = "uid_123",
            email = "jugador@gamedle.com",
            username = "GamerPro",
            profilePictureUrl = "https://example.com/pic.jpg",
            currentStreak = 7
        )

        assertEquals("uid_123", user.id)
        assertEquals("jugador@gamedle.com", user.email)
        assertEquals("GamerPro", user.username)
        assertEquals("https://example.com/pic.jpg", user.profilePictureUrl)
        assertEquals(7, user.currentStreak)
    }

    @Test
    fun user_valoresPorDefectoSonCorrectos() {
        val user = User(id = "uid_456", email = "test@test.com")

        assertNull(user.username)
        assertNull(user.profilePictureUrl)
        assertEquals(0, user.currentStreak)
    }

    @Test
    fun user_dosObjetosConMismosDatosSonIguales() {
        val user1 = User("id1", "a@b.com", "Jugador1")
        val user2 = User("id1", "a@b.com", "Jugador1")

        assertEquals(user1, user2)
    }

    @Test
    fun user_copyActualizaElStreakCorrectamente() {
        val user = User("id1", "a@b.com", currentStreak = 3)
        val actualizado = user.copy(currentStreak = 10)

        assertEquals(10, actualizado.currentStreak)
        assertEquals(user.id, actualizado.id)
        assertEquals(user.email, actualizado.email)
    }

    @Test
    fun user_copyActualizaFotoDePerfil() {
        val user = User("id1", "a@b.com", profilePictureUrl = null)
        val conFoto = user.copy(profilePictureUrl = "https://example.com/new.jpg")

        assertNotNull(conFoto.profilePictureUrl)
        assertEquals("https://example.com/new.jpg", conFoto.profilePictureUrl)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests del modelo GameHint
// ─────────────────────────────────────────────────────────────────────────────

class GameHintModelTest {

    @Test
    fun gameHint_seCreaCorrectamente() {
        val hint = GameHint(
            releaseYear = "1998",
            publisher = "Nintendo",
            genre = "Action-Adventure"
        )

        assertEquals("1998", hint.releaseYear)
        assertEquals("Nintendo", hint.publisher)
        assertEquals("Action-Adventure", hint.genre)
    }

    @Test
    fun gameHint_dosObjetosConMismosDatosSonIguales() {
        val hint1 = GameHint("2001", "Bungie", "Shooter")
        val hint2 = GameHint("2001", "Bungie", "Shooter")

        assertEquals(hint1, hint2)
    }

    @Test
    fun gameHint_dosObjetosConDistintoAnioSonDiferentes() {
        val hint1 = GameHint("2001", "Bungie", "Shooter")
        val hint2 = GameHint("2004", "Bungie", "Shooter")

        assertNotEquals(hint1, hint2)
    }
}
