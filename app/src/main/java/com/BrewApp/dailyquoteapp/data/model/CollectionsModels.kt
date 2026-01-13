package com.BrewApp.dailyquoteapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteCollection(
    val id: Long = 0,
    @SerialName("user_id") val userId: String? = null,
    val name: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CollectionItem(
    val id: Long = 0,
    @SerialName("collection_id") val collectionId: Long,
    val text: String,
    val author: String
)