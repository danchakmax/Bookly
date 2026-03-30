package com.example.bookly.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    val id: Int? = null,
    val text: String,
    val date: String? = null, // Supabase повертає TIMESTAMP як String
    @SerialName("post_id") val postId: Int,
    @SerialName("complainant_id") val complainantId: Int
)