package com.BrewApp.dailyquoteapp.data.repository

import android.util.Log
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import com.BrewApp.dailyquoteapp.data.model.CollectionItem
import com.BrewApp.dailyquoteapp.data.model.QuoteCollection
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from

class CollectionsRepository {
    private val supabase = SupabaseClient.client

    // --- Collections ---
    suspend fun getUserCollections(): List<QuoteCollection> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return emptyList()
            supabase.from("collections").select {
                filter { eq("user_id", userId) }
            }.decodeList()
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error fetching collections", e)
            emptyList()
        }
    }

    suspend fun createCollection(name: String) {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return
            val collection = QuoteCollection(name = name, userId = userId)
            supabase.from("collections").insert(collection)
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error creating collection", e)
        }
    }

    suspend fun deleteCollection(collectionId: Long) {
        try {
            supabase.from("collections").delete {
                filter { eq("id", collectionId) }
            }
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error deleting collection", e)
        }
    }

    // --- Collection Items ---
    suspend fun getQuotesInCollection(collectionId: Long): List<CollectionItem> {
        return try {
            supabase.from("collection_items").select {
                filter { eq("collection_id", collectionId) }
            }.decodeList()
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error fetching items", e)
            emptyList()
        }
    }

    suspend fun addQuoteToCollection(collectionId: Long, text: String, author: String) {
        try {
            val item = CollectionItem(collectionId = collectionId, text = text, author = author)
            supabase.from("collection_items").insert(item)
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error adding quote", e)
        }
    }

    suspend fun removeQuoteFromCollection(itemId: Long) {
        try {
            supabase.from("collection_items").delete {
                filter { eq("id", itemId) }
            }
        } catch (e: Exception) {
            Log.e("CollectionsRepo", "Error removing quote", e)
        }
    }
}