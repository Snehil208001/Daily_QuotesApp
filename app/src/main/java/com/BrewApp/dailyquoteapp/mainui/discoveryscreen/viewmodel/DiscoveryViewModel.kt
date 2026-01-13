package com.BrewApp.dailyquoteapp.mainui.discoveryscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.BrewApp.dailyquoteapp.data.model.SupabaseQuote
import com.BrewApp.dailyquoteapp.data.repository.DiscoveryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoveryViewModel : ViewModel() {

    private val repository = DiscoveryRepository()

    // State
    private val _quotes = MutableStateFlow<List<SupabaseQuote>>(emptyList())
    val quotes: StateFlow<List<SupabaseQuote>> = _quotes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Motivation")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
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

    private fun loadQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedQuotes = repository.getQuotes(
                category = _selectedCategory.value,
                searchQuery = _searchQuery.value
            )
            _quotes.value = fetchedQuotes
            _isLoading.value = false
        }
    }
}