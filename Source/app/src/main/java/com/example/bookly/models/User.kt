package com.example.bookly.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String? = null,
    val password: String, // В реальному проекті пароль краще не тягати в моделях
    val about: String? = null,
    val role: UserRole = UserRole.user,
    val region: String? = null,
    val district: String? = null,
    val city: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)