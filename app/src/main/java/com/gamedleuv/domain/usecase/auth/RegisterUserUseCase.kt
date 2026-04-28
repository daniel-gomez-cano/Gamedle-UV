package com.gamedleuv.domain.usecase.auth

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository

class RegisterUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, username: String): Result<User?> =
        repo.register(email, password, username)
}