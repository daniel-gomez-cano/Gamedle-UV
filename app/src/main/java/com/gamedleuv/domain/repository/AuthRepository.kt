package com.gamedleuv.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun register(email: String, password: String, username: String): Result<FirebaseUser?>
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    fun getCurrentUser(): FirebaseUser?
    fun logout()
}
