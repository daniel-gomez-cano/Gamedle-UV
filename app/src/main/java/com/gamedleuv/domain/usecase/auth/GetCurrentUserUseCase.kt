package com.gamedleuv.domain.usecase.auth;

import com.gamedleuv.domain.model.User
import com.gamedleuv.domain.repository.AuthRepository;

class GetCurrentUserUseCase(
        private val repository:AuthRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUserData()
    }
}