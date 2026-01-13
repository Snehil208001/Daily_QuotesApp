package com.BrewApp.dailyquoteapp.data.repository

import android.util.Log
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import com.BrewApp.dailyquoteapp.data.model.SupabaseQuote
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class DiscoveryRepository {

    private val supabase = SupabaseClient.client

    suspend fun getQuotes(
        category: String,
        searchQuery: String = ""
    ): List<SupabaseQuote> {
        return try {
            val result = supabase.from("quotes").select(
                columns = Columns.list("id", "text", "author", "category")
            ) {
                filter {
                    // Filter by Category
                    eq("category", category)

                    // Search functionality (Case-insensitive search on text OR author)
                    if (searchQuery.isNotEmpty()) {
                        or {
                            ilike("text", "%$searchQuery%")
                            ilike("author", "%$searchQuery%")
                        }
                    }
                }
                // Order by ID descending (newest first)
                order("id", order = Order.DESCENDING)
            }
            result.decodeList<SupabaseQuote>()
        } catch (e: Exception) {
            Log.e("DiscoveryRepo", "Error fetching quotes: ${e.message}")
            emptyList()
        }
    }
}