package com.example.bookly.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookGenre(
    @SerialName("post_id") val postId: Int,
    @SerialName("genre_id") val genreId: Int
)