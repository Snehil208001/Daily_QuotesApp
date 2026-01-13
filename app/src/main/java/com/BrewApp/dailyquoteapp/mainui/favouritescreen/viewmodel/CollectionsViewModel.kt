package com.BrewApp.dailyquoteapp.mainui.favouritescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.BrewApp.dailyquoteapp.data.model.CollectionItem
import com.BrewApp.dailyquoteapp.data.model.QuoteCollection
import com.BrewApp.dailyquoteapp.data.repository.CollectionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel : ViewModel() {
    private val repository = CollectionsRepository()

    private val _collections = MutableStateFlow<List<QuoteCollection>>(emptyList())
    val collections: StateFlow<List<QuoteCollection>> = _collections.asStateFlow()

    private val _collectionItems = MutableStateFlow<List<CollectionItem>>(emptyList())
    val collectionItems: StateFlow<List<CollectionItem>> = _collectionItems.asStateFlow()

    // --- FIX: Load data immediately when ViewModel is created ---
    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            _collections.value = repository.getUserCollections()
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch {
            repository.createCollection(name)
            loadCollections() // Refresh list
        }
    }

    fun deleteCollection(id: Long) {
        viewModelScope.launch {
            repository.deleteCollection(id)
            loadCollections()
        }
    }

    // --- Items Logic ---
    fun loadQuotesForCollection(collectionId: Long) {
        viewModelScope.launch {
            _collectionItems.value = repository.getQuotesInCollection(collectionId)
        }
    }

    fun addQuoteToCollection(collection: QuoteCollection, text: String, author: String) {
        viewModelScope.launch {
            repository.addQuoteToCollection(collection.id, text, author)
        }
    }

    fun removeQuoteFromCollection(itemId: Long, collectionId: Long) {
        viewModelScope.launch {
            repository.removeQuoteFromCollection(itemId)
            loadQuotesForCollection(collectionId)
        }
    }
}