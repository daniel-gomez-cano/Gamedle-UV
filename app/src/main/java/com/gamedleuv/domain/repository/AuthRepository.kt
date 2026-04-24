package com.gamedleuv.domain.repository

import com.gamedleuv.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String
    ): Result<User?>
    suspend fun login(email: String, password: String): Result<User?>
    fun getCurrentUser(): User?
    fun logout()
}

