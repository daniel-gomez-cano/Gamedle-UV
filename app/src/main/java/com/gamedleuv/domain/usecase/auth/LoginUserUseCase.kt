package com.gamedleuv.domain.usecase.auth

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository

class LoginUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User?> =
        repo.login(email, password)
}
