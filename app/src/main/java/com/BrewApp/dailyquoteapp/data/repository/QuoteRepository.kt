package com.BrewApp.dailyquoteapp.data.repository

import android.util.Log
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import com.BrewApp.dailyquoteapp.data.db.FavoriteDao
import com.BrewApp.dailyquoteapp.data.db.FavoriteQuote
import com.BrewApp.dailyquoteapp.data.model.Quote
import com.BrewApp.dailyquoteapp.data.network.RetrofitInstance
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudFavorite(
    val id: Long = 0,
    @SerialName("user_id") val userId: String,
    val text: String,
    val author: String
)

class QuoteRepository(private val favoriteDao: FavoriteDao) {
    private val api = RetrofitInstance.api
    private val supabase = SupabaseClient.client

    // --- API OPERATIONS ---
    suspend fun fetchRandomQuotes(): List<Quote> {
        return try {
            api.getQuotes()
        } catch (e: Exception) {
            listOf(Quote("Failure is simply the opportunity to begin again.", "Henry Ford"))
        }
    }

    // --- SYNCED DATABASE OPERATIONS ---

    // 1. Sync: Fetch from Cloud, update Local
    suspend fun syncFavorites() {
        try {
            val userId = supabase.auth.currentUserOrNull()?.id

            if (userId == null) {
                Log.e("QuoteRepository", "Sync failed: User is not logged in.")
                return
            }
            Log.d("QuoteRepository", "Starting sync for user: $userId")

            // A. Fetch all favorites from Supabase Cloud
            val cloudFavorites = supabase.from("user_favorites").select {
                filter { eq("user_id", userId) }
            }.decodeList<CloudFavorite>()

            Log.d("QuoteRepository", "Fetched ${cloudFavorites.size} favorites from Cloud.")

            // B. Get current Local favorites to prevent duplicates
            val localFavorites = favoriteDao.getAllFavorites().first()
            val localTexts = localFavorites.map { it.text }.toSet()

            // C. Insert only new items
            var addedCount = 0
            cloudFavorites.forEach { cloudFav ->
                if (!localTexts.contains(cloudFav.text)) {
                    favoriteDao.insert(FavoriteQuote(text = cloudFav.text, author = cloudFav.author))
                    addedCount++
                }
            }
            Log.d("QuoteRepository", "Sync complete. Added $addedCount new quotes to local DB.")

        } catch (e: Exception) {
            Log.e("QuoteRepository", "Sync FAILED: ${e.message}", e)
        }
    }

    // 2. Toggle: Update Cloud AND Local
    suspend fun toggleFavorite(quote: Quote, isFavorite: Boolean) {
        val userId = supabase.auth.currentUserOrNull()?.id

        if (isFavorite) {
            // Remove from Local
            favoriteDao.deleteByText(quote.text)
            // Remove from Cloud
            if (userId != null) {
                try {
                    supabase.from("user_favorites").delete {
                        filter {
                            eq("user_id", userId)
                            eq("text", quote.text)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("QuoteRepository", "Error deleting from cloud: ${e.message}")
                }
            }
        } else {
            // Add to Local
            favoriteDao.insert(FavoriteQuote(text = quote.text, author = quote.author))
            // Add to Cloud
            if (userId != null) {
                try {
                    val cloudFav = CloudFavorite(userId = userId, text = quote.text, author = quote.author)
                    supabase.from("user_favorites").insert(cloudFav)
                } catch (e: Exception) {
                    Log.e("QuoteRepository", "Error adding to cloud: ${e.message}")
                }
            }
        }
    }

    fun isQuoteFavorite(text: String): Flow<Boolean> = favoriteDao.isFavorite(text)
    fun getAllFavorites(): Flow<List<FavoriteQuote>> = favoriteDao.getAllFavorites()
}