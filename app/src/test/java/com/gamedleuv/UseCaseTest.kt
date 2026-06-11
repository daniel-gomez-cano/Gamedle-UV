package com.gamedleuv

import com.gamedleuv.domain.model.Game
import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository
import com.gamedleuv.domain.repository.GameRepository
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.auth.ResetPasswordUserCase
import com.gamedleuv.domain.usecase.auth.UploadProfilePictureUseCase
import com.gamedleuv.domain.usecase.game.GetRandomGameUseCase
import com.gamedleuv.domain.usecase.game.SearchGamesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// ─────────────────────────────────────────────────────────────────────────────
// Repositorios falsos (Fakes) reutilizados en todos los tests de use cases
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Implementación falsa de AuthRepository que permite configurar los resultados
 * que devolverá cada método para simular diferentes escenarios sin Firebase.
 */
class FakeAuthRepository : AuthRepository {
    var loginResult: Result<User?> = Result.success(null)
    var registerResult: Result<User?> = Result.success(null)
    var resetPasswordResult: Result<Unit> = Result.success(Unit)
    var uploadPictureResult: Result<String> = Result.success("https://example.com/photo.jpg")

    override suspend fun login(email: String, password: String) = loginResult
    override suspend fun register(email: String, password: String, username: String) = registerResult
    override fun getCurrentUser(): User? = null
    override fun logout() {}
    override suspend fun updateStreakIfHigher(uid: String, newStreak: Int) = Result.success(Unit)
    override suspend fun sendPasswordReset(email: String) = resetPasswordResult
    override suspend fun uploadProfilePicture(uid: String, imageBytes: ByteArray) = uploadPictureResult
    override suspend fun updateProfilePictureUrl(uid: String, url: String) = Result.success(Unit)
    override suspend fun getCurrentUserData(): User? {
        TODO("Not yet implemented")
    }
}

/**
 * Implementación falsa de GameRepository que permite configurar los resultados
 * de búsqueda y juego aleatorio sin llamar a la API de IGDB.
 */
class FakeGameRepository : GameRepository {
    var searchResult: List<Game> = emptyList()
    var randomGameResult: Game? = null

    override suspend fun searchGames(query: String) = searchResult
    override suspend fun getRandomGame() = randomGameResult
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de LoginUserUseCase
// ─────────────────────────────────────────────────────────────────────────────

class LoginUserUseCaseTest {
    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: LoginUserUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = LoginUserUseCase(fakeRepo)
    }

    @Test
    fun login_exitosoRetornaElUsuario() = runTest {
        val user = User("uid1", "jugador@test.com", "Jugador")
        fakeRepo.loginResult = Result.success(user)

        val result = useCase("jugador@test.com", "password123")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun login_fallidoRetornaFailureConMensajeDeError() = runTest {
        fakeRepo.loginResult = Result.failure(Exception("Credenciales inválidas"))

        val result = useCase("malo@test.com", "wrongpass")

        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas", result.exceptionOrNull()?.message)
    }

    @Test
    fun login_conUsuarioNullRetornaSuccessConNull() = runTest {
        fakeRepo.loginResult = Result.success(null)

        val result = useCase("test@test.com", "pass")

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de RegisterUserUseCase
// ─────────────────────────────────────────────────────────────────────────────

class RegisterUserUseCaseTest {
    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: RegisterUserUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = RegisterUserUseCase(fakeRepo)
    }

    @Test
    fun register_exitosoRetornaElUsuarioCreado() = runTest {
        val user = User("uid2", "nuevo@test.com", "NuevoJugador")
        fakeRepo.registerResult = Result.success(user)

        val result = useCase("nuevo@test.com", "pass123", "NuevoJugador")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        assertEquals("NuevoJugador", result.getOrNull()?.username)
    }

    @Test
    fun register_conEmailDuplicadoRetornaFailure() = runTest {
        fakeRepo.registerResult = Result.failure(Exception("El email ya está en uso"))

        val result = useCase("existente@test.com", "pass", "User")

        assertTrue(result.isFailure)
        assertEquals("El email ya está en uso", result.exceptionOrNull()?.message)
    }

    @Test
    fun register_errorGenericoRetornaFailure() = runTest {
        fakeRepo.registerResult = Result.failure(RuntimeException("Error de red"))

        val result = useCase("test@test.com", "pass", "test")

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de GetRandomGameUseCase
// ─────────────────────────────────────────────────────────────────────────────

class GetRandomGameUseCaseTest {
    private lateinit var fakeRepo: FakeGameRepository
    private lateinit var useCase: GetRandomGameUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeGameRepository()
        useCase = GetRandomGameUseCase(fakeRepo)
    }

    @Test
    fun getRandomGame_retornaElJuegoCuandoElRepositorioTieneUno() = runTest {
        val game = Game(42, "Super Mario Bros", null, "1985", "Nintendo", "Platform")
        fakeRepo.randomGameResult = game

        val result = useCase()

        assertNotNull(result)
        assertEquals(game, result)
        assertEquals("Super Mario Bros", result?.name)
    }

    @Test
    fun getRandomGame_retornaNullCuandoElRepositorioNoTieneJuegos() = runTest {
        fakeRepo.randomGameResult = null

        val result = useCase()

        assertNull(result)
    }

    @Test
    fun getRandomGame_retornaJuegoConCamposNullos() = runTest {
        val game = Game(99, "Juego Misterioso", null, null, null, null)
        fakeRepo.randomGameResult = game

        val result = useCase()

        assertNotNull(result)
        assertNull(result?.imageUrl)
        assertNull(result?.publisher)
        assertNull(result?.genre)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de SearchGamesUseCase
// ─────────────────────────────────────────────────────────────────────────────

class SearchGamesUseCaseTest {
    private lateinit var fakeRepo: FakeGameRepository
    private lateinit var useCase: SearchGamesUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeGameRepository()
        useCase = SearchGamesUseCase(fakeRepo)
    }

    @Test
    fun search_retornaLaListaDelRepositorio() = runTest {
        val games = listOf(
            Game(1, "Minecraft", null, "2011", "Mojang", "Sandbox"),
            Game(2, "Minecraft Dungeons", null, "2020", "Mojang", "Action")
        )
        fakeRepo.searchResult = games

        val result = useCase("Minecraft")

        assertEquals(2, result.size)
        assertEquals("Minecraft", result[0].name)
        assertEquals("Minecraft Dungeons", result[1].name)
    }

    @Test
    fun search_sinResultadosRetornaListaVacia() = runTest {
        fakeRepo.searchResult = emptyList()

        val result = useCase("xyzjuegoinexistente")

        assertTrue(result.isEmpty())
    }

    @Test
    fun search_retornaUnSoloJuego() = runTest {
        fakeRepo.searchResult = listOf(
            Game(5, "Halo", null, "2001", "Bungie", "Shooter")
        )

        val result = useCase("Halo")

        assertEquals(1, result.size)
        assertEquals("Halo", result[0].name)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de ResetPasswordUserCase
// ─────────────────────────────────────────────────────────────────────────────

class ResetPasswordUseCaseTest {
    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: ResetPasswordUserCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = ResetPasswordUserCase(fakeRepo)
    }

    @Test
    fun resetPassword_exitosoRetornaSuccess() = runTest {
        fakeRepo.resetPasswordResult = Result.success(Unit)

        val result = useCase("jugador@test.com")

        assertTrue(result.isSuccess)
    }

    @Test
    fun resetPassword_emailNoEncontradoRetornaFailure() = runTest {
        fakeRepo.resetPasswordResult = Result.failure(Exception("Email no encontrado"))

        val result = useCase("noexiste@test.com")

        assertTrue(result.isFailure)
        assertEquals("Email no encontrado", result.exceptionOrNull()?.message)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tests de UploadProfilePictureUseCase
// ─────────────────────────────────────────────────────────────────────────────

class UploadProfilePictureUseCaseTest {
    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: UploadProfilePictureUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = UploadProfilePictureUseCase(fakeRepo)
    }

    @Test
    fun upload_exitosoRetornaLaUrl() = runTest {
        val expectedUrl = "https://storage.firebase.com/users/uid1/profile.jpg"
        fakeRepo.uploadPictureResult = Result.success(expectedUrl)

        val result = useCase("uid1", ByteArray(0))

        assertTrue(result.isSuccess)
        assertEquals(expectedUrl, result.getOrNull())
    }

    @Test
    fun upload_fallidoRetornaFailure() = runTest {
        fakeRepo.uploadPictureResult = Result.failure(Exception("Error al subir imagen"))

        val result = useCase("uid1", ByteArray(0))

        assertTrue(result.isFailure)
        assertEquals("Error al subir imagen", result.exceptionOrNull()?.message)
    }
}
