package com.gamedleuv.domain.model

data class User(
    val id: String,
    val email: String?,
    val username: String? = null,
    val profilePictureUrl: String? = null,
    val currentStreak: Int = 0
)
