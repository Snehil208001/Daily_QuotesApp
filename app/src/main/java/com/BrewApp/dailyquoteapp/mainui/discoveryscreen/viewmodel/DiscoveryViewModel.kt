package com.BrewApp.dailyquoteapp.mainui.discoveryscreen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.BrewApp.dailyquoteapp.data.db.AppDatabase
import com.BrewApp.dailyquoteapp.data.model.Quote
import com.BrewApp.dailyquoteapp.data.model.SupabaseQuote
import com.BrewApp.dailyquoteapp.data.repository.DiscoveryRepository
import com.BrewApp.dailyquoteapp.data.repository.QuoteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscoveryViewModel(application: Application) : AndroidViewModel(application) {

    private val discoveryRepository = DiscoveryRepository()
    private val quoteRepository: QuoteRepository

    // Internal Raw states
    private val _rawQuotes = MutableStateFlow<List<SupabaseQuote>>(emptyList())

    // Public exposed state (combined with favorites)
    val quotes: StateFlow<List<SupabaseQuote>>

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Motivation")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Initialize local database repository
        val database = AppDatabase.getDatabase(application)
        quoteRepository = QuoteRepository(database.favoriteDao())

        // Combine the fetched quotes with the local favorites to determine 'isLiked' state
        quotes = combine(_rawQuotes, quoteRepository.getAllFavorites()) { serverQuotes, favorites ->
            serverQuotes.map { quote ->
                // Check if this quote exists in the local favorites list by text
                quote.copy(isLiked = favorites.any { it.text == quote.text })
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        loadQuotes()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        loadQuotes()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce search input
            loadQuotes()
        }
    }

    fun refresh() {
        loadQuotes()
    }

    // New Function: Handle Toggling Favorites
    fun toggleQuoteLike(supabaseQuote: SupabaseQuote) {
        viewModelScope.launch {
            val quote = Quote(text = supabaseQuote.text, author = supabaseQuote.author)
            // If it is currently liked, we want to unlike it (isFavorite=true deletes it)
            // If it is not liked, we want to like it (isFavorite=false inserts it)
            quoteRepository.toggleFavorite(quote, isFavorite = supabaseQuote.isLiked)
        }
    }

    private fun loadQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            // Fetch data directly from Supabase
            val fetchedQuotes = discoveryRepository.getQuotes(
                category = _selectedCategory.value,
                searchQuery = _searchQuery.value
            )
            _rawQuotes.value = fetchedQuotes
            _isLoading.value = false
        }
    }
}