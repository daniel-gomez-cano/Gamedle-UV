package com.gamedleuv.domain.usecase.auth

import com.gamedleuv.domain.repository.AuthRepository

class UploadProfilePictureUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(uid: String, imageBytes: ByteArray): Result<String> {
        val uploadResult = repository.uploadProfilePicture(uid, imageBytes)
        if (uploadResult.isSuccess) {
            val url = uploadResult.getOrNull()!!
            repository.updateProfilePictureUrl(uid, url)
        }
        return uploadResult
    }
}