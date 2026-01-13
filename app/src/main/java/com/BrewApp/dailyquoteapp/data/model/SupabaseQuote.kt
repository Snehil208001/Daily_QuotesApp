package com.BrewApp.dailyquoteapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseQuote(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("text")
    val text: String,

    @SerialName("author")
    val author: String,

    @SerialName("category")
    val category: String,

    // Optional field for UI state
    val isLiked: Boolean = false
)