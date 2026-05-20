package com.gamedleuv.domain.repository

import com.gamedleuv.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String
    ): Result<User?>
    suspend fun login(email: String, password: String): Result<User?>
    fun getCurrentUser(): User?
    fun logout()
    suspend fun updateStreakIfHigher(uid: String, newStreak: Int): Result<Unit>

    suspend fun uploadProfilePicture(uid: String, imageBytes: ByteArray): Result<String>
    suspend fun updateProfilePictureUrl(uid: String, url: String): Result<Unit>
}

