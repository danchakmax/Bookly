package com.example.bookly.models

import kotlinx.serialization.Serializable

@Serializable
enum class DealType {
    exchange,
    donation
}