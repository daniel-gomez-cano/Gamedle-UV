package com.gamedleuv.data.repository

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private suspend fun fetchUserFromFirestore(uid: String, email: String?): User {
        val doc = firestore.collection("users").document(uid).get().await()
        val username = doc.getString("username")
        val streak = doc.getLong("currentStreak")?.toInt() ?: 0
        return User(id = uid, email = email, username = username, currentStreak = streak)
    }

    override suspend fun register(email: String, password: String, username: String): Result<User?> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                try {
                    val userData = mapOf(
                        "uid"               to firebaseUser.uid,
                        "email"             to email,
                        "username"          to username,
                        "createdAt"         to System.currentTimeMillis(),
                        "currentStreak"     to 0,
                        "profilePictureUrl" to ""
                    )
                    firestore.collection("users").document(firebaseUser.uid).set(userData).await()
                    Result.success(User(id = firebaseUser.uid, email = email, username = username))
                } catch (firestoreException: Exception) {
                    try { firebaseUser.delete().await() }
                    catch (deleteException: Exception) {
                        firestoreException.addSuppressed(deleteException)
                    }
                    Result.failure(firestoreException)
                }
            } else {
                Result.failure(IllegalStateException("User registration completed without creating a Firebase user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User?> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            val user = if (firebaseUser != null)
                fetchUserFromFirestore(firebaseUser.uid, firebaseUser.email)
            else null
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Guarda la racha solo si es mayor a la almacenada en Firestore
    override suspend fun updateStreakIfHigher(uid: String, newStreak: Int): Result<Unit> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val savedStreak = doc.getLong("currentStreak")?.toInt() ?: 0
            if (newStreak > savedStreak) {
                firestore.collection("users").document(uid)
                    .update("currentStreak", newStreak)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return User(id = firebaseUser.uid, email = firebaseUser.email, username = null)
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}