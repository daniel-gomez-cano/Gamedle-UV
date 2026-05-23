package com.gamedleuv.ui.viewmodel

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.auth.ResetPasswordUserCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUser: RegisterUserUseCase,
    private val loginUser: LoginUserUseCase,
    private val resetPassword: ResetPasswordUserCase,
    private val scope: CoroutineScope
) {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(email: String, password: String) {
        scope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUser(email, password)
            _uiState.value = if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                AuthUiState.Success("Sesión iniciada :D")
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error :/")
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        scope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerUser(email, password, username)
            _uiState.value = if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                AuthUiState.Success("Usuario registrado piola")
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error :/")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun sendPasswordReset(email: String) {
        scope.launch {
            _uiState.value = AuthUiState.Loading
            val result = resetPassword(email)
            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success("Correo enviado")
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val msg: String) : AuthUiState()
    data class Error(val error: String) : AuthUiState()
}
