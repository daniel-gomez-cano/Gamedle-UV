package com.gamedleuv.data.repository

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private fun FirebaseUser.toDomain(): User {
        return User(
            id = uid,
            email = email,
            username = displayName 
        )
    }

    override suspend fun register(email: String, password: String, username: String): Result<User?> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                try {
                    val userData = mapOf(
                        "uid" to firebaseUser.uid,
                        "email" to email,
                        "username" to username,
                        "createdAt" to System.currentTimeMillis(),
                        "currentStreak" to 0,
                        "profilePictureUrl" to ""
                    )
                    firestore.collection("users").document(firebaseUser.uid).set(userData).await()
                    Result.success(User(id = firebaseUser.uid, email = email, username = username))
                } catch (firestoreException: Exception) {
                    firebaseUser.delete().await()
                    return Result.failure(firestoreException)
                }
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User?> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toDomain()
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
