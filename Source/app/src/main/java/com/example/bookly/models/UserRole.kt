package com.example.bookly.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    user,
    admin
}