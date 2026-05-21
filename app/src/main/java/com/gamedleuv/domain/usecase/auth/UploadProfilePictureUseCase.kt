package com.gamedleuv.domain.usecase.auth

import com.gamedleuv.domain.repository.AuthRepository

class UploadProfilePictureUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String, imageBytes: ByteArray): Result<String> {
        return repository.uploadProfilePicture(uid, imageBytes)
    }
}