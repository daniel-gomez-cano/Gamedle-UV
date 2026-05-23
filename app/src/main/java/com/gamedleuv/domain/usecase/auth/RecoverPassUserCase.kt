package com.gamedleuv.domain.usecase.auth

import com.gamedleuv.domain.repository.AuthRepository

class ResetPasswordUserCase(private val repository: AuthRepository)
{
    suspend operator fun invoke(email: String) =
        repository.sendPasswordReset(email) //Utiliza el mismo firebase para mandarle la recuperacion de contraseña al usuario
}