package com.gamedleuv.util

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.toSpanishErrorMessage(): String {
    return when (this) {
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Usa al menos 6 caracteres."
        is FirebaseAuthInvalidCredentialsException -> {
            when (errorCode) {
                "ERROR_INVALID_EMAIL", "auth/invalid-email" -> "El formato del correo electrónico no es válido."
                "ERROR_WRONG_PASSWORD", "auth/wrong-password" -> "La contraseña es incorrecta."
                else -> "Credenciales inválidas. Revisa tu correo y contraseña."
            }
        }
        is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo electrónico."
        is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado en otra cuenta."
        is FirebaseAuthException -> {
            when (errorCode) {
                "ERROR_USER_DISABLED", "auth/user-disabled" -> "Esta cuenta ha sido deshabilitada."
                "ERROR_TOO_MANY_REQUESTS", "auth/too-many-requests" -> "Demasiados intentos fallidos. Inténtalo más tarde."
                "auth/network-request-failed" -> "Error de conexión. Revisa tu internet."
                else -> "Error de autenticación: ${localizedMessage ?: "Inténtalo de nuevo"}"
            }
        }
        is IllegalArgumentException -> {
            if (message?.contains("empty", ignoreCase = true) == true) {
                "Por favor, completa todos los campos."
            } else {
                "Datos no válidos: $message"
            }
        }
        else -> {
            val msg = message ?: ""
            when {
                msg.contains("badly formatted", ignoreCase = true) -> "El correo electrónico está mal escrito."
                msg.contains("empty or null", ignoreCase = true) -> "No puedes dejar campos vacíos."
                msg.contains("incorrect, malformed or has expired", ignoreCase = true) -> "Credenciales incorrectas o expiradas."
                else -> "Ha ocurrido un error inesperado. Inténtalo de nuevo."
            }
        }
    }
}
