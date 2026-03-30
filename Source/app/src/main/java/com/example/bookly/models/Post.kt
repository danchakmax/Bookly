package com.example.bookly.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Int? = null,
    @SerialName("user_id") val userId: Int,
    val title: String,
    val author: String? = null,
    @SerialName("deal_type") val dealType: DealType? = null,
    val description: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)