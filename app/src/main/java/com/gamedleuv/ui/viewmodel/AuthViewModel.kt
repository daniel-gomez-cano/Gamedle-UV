package com.gamedleuv.ui.viewmodel

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.usecase.auth.LoginUserUseCase
import com.gamedleuv.domain.usecase.auth.RegisterUserUseCase
import com.gamedleuv.domain.usecase.auth.ResetPasswordUserCase
import com.gamedleuv.domain.usecase.auth.UploadProfilePictureUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUser: RegisterUserUseCase,
    private val loginUser: LoginUserUseCase,
    private val resetPassword: ResetPasswordUserCase,
    private val uploadProfilePicture: UploadProfilePictureUseCase,
    private val scope: CoroutineScope  //Fix Copilot: scope inyectado externamente
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

    private val _profilePictureState = MutableStateFlow<ProfilePictureState>(ProfilePictureState.Idle)
    val profilePictureState: StateFlow<ProfilePictureState> = _profilePictureState

    fun uploadProfilePicture(imageBytes: ByteArray) {
        val uid = _currentUser.value?.id ?: return
        scope.launch {
            _profilePictureState.value = ProfilePictureState.Loading
            val result = uploadProfilePicture(uid, imageBytes)
            if (result.isSuccess) {
                val url = result.getOrNull()!!
                // Actualiza el usuario en memoria con la nueva URL
                android.util.Log.d("PHOTO_DEBUG", "URL subida: $url")
                _currentUser.value = _currentUser.value?.copy(profilePictureUrl = url)
                _profilePictureState.value = ProfilePictureState.Success
            } else {
                android.util.Log.e("PHOTO_DEBUG", "Error: ${result.exceptionOrNull()?.message}")
                _profilePictureState.value = ProfilePictureState.Error(
                    result.exceptionOrNull()?.message ?: "Error subiendo foto"
                )
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



sealed class ProfilePictureState {
    object Idle : ProfilePictureState()
    object Loading : ProfilePictureState()
    object Success : ProfilePictureState()
    data class Error(val message: String) : ProfilePictureState()
}
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val msg: String) : AuthUiState()
    data class Error(val error: String) : AuthUiState()
}
