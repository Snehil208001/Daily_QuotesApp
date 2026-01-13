package com.BrewApp.dailyquoteapp.mainui.profilescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.BrewApp.dailyquoteapp.data.auth.AuthManager
import com.BrewApp.dailyquoteapp.data.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val authManager = AuthManager()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    // Added State for Full Name
    private val _fullName = MutableStateFlow<String?>(null)
    val fullName: StateFlow<String?> = _fullName.asStateFlow()

    // NEW: Added State for Avatar URL
    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    // NEW: State for upload loading status
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _userEmail.value = authManager.getCurrentUserEmail()
            // Fetch and set full name
            _fullName.value = authManager.getCurrentUserName()
            // Fetch and set avatar URL
            _avatarUrl.value = authManager.getCurrentUserAvatar()
        }
    }

    // NEW: Function to upload avatar
    fun uploadAvatar(imageBytes: ByteArray) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                // 1. Upload to Storage
                val publicUrl = authManager.uploadProfilePicture(imageBytes)

                // 2. Update User Metadata
                authManager.updateUserAvatarUrl(publicUrl)

                // 3. Update Local State
                _avatarUrl.value = publicUrl
                _profileState.value = ProfileState.Success("Profile picture updated!")
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to upload image")
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            when (val result = authManager.signOut()) {
                is AuthResult.Success -> {
                    _profileState.value = ProfileState.LogoutSuccess
                }
                is AuthResult.Error -> {
                    _profileState.value = ProfileState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _profileState.value = ProfileState.Idle
    }
}

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    object LogoutSuccess : ProfileState()
    data class Error(val message: String) : ProfileState()
    // Added for generic success messages (like upload success)
    data class Success(val message: String) : ProfileState()
}