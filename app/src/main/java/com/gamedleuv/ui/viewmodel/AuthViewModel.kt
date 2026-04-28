package com.gamedleuv.ui.viewmodel

import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUser: RegisterUserUseCase,
    private val loginUser: LoginUserUseCase,
    private val scope: CoroutineScope  //Fix Copilot: scope inyectado externamente
) {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        scope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUser(email, password)
            _uiState.value = if (result.isSuccess) {
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
                AuthUiState.Success("Usuario registrado piola")
            } else {
                AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error :/")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val msg: String) : AuthUiState()
    data class Error(val error: String) : AuthUiState()
}