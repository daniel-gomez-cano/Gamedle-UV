package com.gamedleuv

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.auth.ResetPasswordUserCase
import com.gamedleuv.domain.usecase.auth.UploadProfilePictureUseCase
import com.gamedleuv.ui.viewmodel.AuthUiState
import com.gamedleuv.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para AuthViewModel.
 * Esto está escrito por Claude ↓
 * Se utiliza UnconfinedTestDispatcher para que las corrutinas lanzadas dentro
 * del ViewModel se ejecuten de forma síncrona durante los tests, permitiendo
 * verificar el estado resultante inmediatamente después de cada acción.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        // Reemplaza el dispatcher Main por uno de test (sin Android)
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeAuthRepository()
        viewModel = AuthViewModel(
            registerUser = RegisterUserUseCase(fakeRepo),
            loginUser = LoginUserUseCase(fakeRepo),
            resetPassword = ResetPasswordUserCase(fakeRepo),
            uploadProfilePicture = UploadProfilePictureUseCase(fakeRepo),
            scope = CoroutineScope(testDispatcher),
            getCurrentUser = TODO()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Estado inicial ───────────────────────────────────────────────────────

    @Test
    fun estadoInicial_esIdleYSinUsuario() {
        assertTrue(viewModel.uiState.value is AuthUiState.Idle)
        assertNull(viewModel.currentUser.value)
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @Test
    fun login_exitosoCambiaEstadoASuccessYActualizaCurrentUser() = runTest {
        val user = User("uid1", "jugador@gamedle.com", "Jugador")
        fakeRepo.loginResult = Result.success(user)

        viewModel.login("jugador@gamedle.com", "password123")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals("Sesión iniciada :D", (state as AuthUiState.Success).msg)
        assertEquals(user, viewModel.currentUser.value)
    }

    @Test
    fun login_fallidoCambiaEstadoAError() = runTest {
        fakeRepo.loginResult = Result.failure(Exception("Usuario o contraseña incorrectos"))

        viewModel.login("malo@test.com", "wrongpass")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Usuario o contraseña incorrectos", (state as AuthUiState.Error).error)
    }

    @Test
    fun login_sinMensajeDeErrorUsaTextoGenerico() = runTest {
        fakeRepo.loginResult = Result.failure(Exception()) // sin mensaje

        viewModel.login("a@b.com", "pass")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("Error :/", (state as AuthUiState.Error).error)
    }

    @Test
    fun login_noActualizaCurrentUserCuandoFalla() = runTest {
        fakeRepo.loginResult = Result.failure(Exception("Error"))

        viewModel.login("a@b.com", "pass")

        assertNull(viewModel.currentUser.value)
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    @Test
    fun register_exitosoCambiaEstadoASuccessYActualizaCurrentUser() = runTest {
        val user = User("uid2", "nuevo@gamedle.com", "Nuevo")
        fakeRepo.registerResult = Result.success(user)

        viewModel.register("nuevo@gamedle.com", "pass123", "Nuevo")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals(user, viewModel.currentUser.value)
    }

    @Test
    fun register_fallidoCambiaEstadoAError() = runTest {
        fakeRepo.registerResult = Result.failure(Exception("El email ya está registrado"))

        viewModel.register("existente@test.com", "pass", "User")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals("El email ya está registrado", (state as AuthUiState.Error).error)
    }

    // ─── Reset state ──────────────────────────────────────────────────────────

    @Test
    fun resetState_despuesDeErrorVuelveEstadoAIdle() = runTest {
        fakeRepo.loginResult = Result.failure(Exception("Error"))
        viewModel.login("a@b.com", "pass")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)

        viewModel.resetState()

        assertTrue(viewModel.uiState.value is AuthUiState.Idle)
    }

    @Test
    fun resetState_despuesDeSuccessVuelveEstadoAIdle() = runTest {
        val user = User("uid3", "a@b.com")
        fakeRepo.loginResult = Result.success(user)
        viewModel.login("a@b.com", "pass")
        assertTrue(viewModel.uiState.value is AuthUiState.Success)

        viewModel.resetState()

        assertTrue(viewModel.uiState.value is AuthUiState.Idle)
    }

    // ─── SendPasswordReset ────────────────────────────────────────────────────

    @Test
    fun sendPasswordReset_exitosoCambiaEstadoASuccess() = runTest {
        fakeRepo.resetPasswordResult = Result.success(Unit)

        viewModel.sendPasswordReset("jugador@gamedle.com")

        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Success)
        assertEquals("Correo enviado", (state as AuthUiState.Success).msg)
    }

    @Test
    fun sendPasswordReset_fallidoCambiaEstadoAError() = runTest {
        fakeRepo.resetPasswordResult = Result.failure(Exception("Email no registrado"))

        viewModel.sendPasswordReset("noexiste@test.com")

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }
}
