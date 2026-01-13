package com.BrewApp.dailyquoteapp.data.auth

import android.util.Log
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

class AuthManager {
    // Access the client via the Singleton
    val supabase = SupabaseClient.client

    // Check if user is logged in
    suspend fun isUserLoggedIn(): Boolean {
        return try {
            supabase.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            Log.e("AuthManager", "Error checking login status", e)
            false
        }
    }

    // Get current user email
    suspend fun getCurrentUserEmail(): String? {
        return try {
            supabase.auth.currentUserOrNull()?.email
        } catch (e: Exception) {
            Log.e("AuthManager", "Error getting user email", e)
            null
        }
    }

    // Get current user full name
    suspend fun getCurrentUserName(): String? {
        return try {
            val user = supabase.auth.currentUserOrNull()
            // Retrieve 'full_name' from user metadata
            user?.userMetadata?.get("full_name")?.jsonPrimitive?.contentOrNull
        } catch (e: Exception) {
            Log.e("AuthManager", "Error getting user name", e)
            null
        }
    }

    // Get current user avatar URL
    suspend fun getCurrentUserAvatar(): String? {
        return try {
            val user = supabase.auth.currentUserOrNull()
            // Retrieve 'avatar_url' from user metadata
            user?.userMetadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull
        } catch (e: Exception) {
            Log.e("AuthManager", "Error getting user avatar", e)
            null
        }
    }

    // Upload profile picture to Supabase Storage and return Public URL
    suspend fun uploadProfilePicture(imageBytes: ByteArray): String {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not logged in")
        val userId = user.id
        // Create a unique filename for the user
        val fileName = "$userId/avatar_${System.currentTimeMillis()}.png"

        val bucket = supabase.storage.from("avatars") // Ensure bucket "avatars" exists in Supabase

        // FIXED: Using named argument 'upsert = true' which matches the library signature (String, ByteArray, Boolean)
        bucket.upload(fileName, imageBytes, upsert = true)

        return bucket.publicUrl(fileName)
    }

    // Update user metadata with new Avatar URL
    suspend fun updateUserAvatarUrl(url: String) {
        val user = supabase.auth.currentUserOrNull() ?: throw Exception("User not logged in")
        val updatedMetadata = buildJsonObject {
            put("avatar_url", url)
        }
        // Using modifyUser (Correct function for Supabase-kt v2+)
        supabase.auth.modifyUser {
            data = updatedMetadata
        }
    }

    // Sync local preferences to Supabase User Metadata
    suspend fun syncUserPreferences(theme: String, accent: String, fontScale: Float) {
        try {
            val user = supabase.auth.currentUserOrNull() ?: return
            val metadata = buildJsonObject {
                put("pref_theme", theme)
                put("pref_accent", accent)
                put("pref_font_scale", fontScale)
            }
            supabase.auth.modifyUser {
                data = metadata
            }
            Log.d("AuthManager", "Preferences synced to cloud")
        } catch (e: Exception) {
            Log.e("AuthManager", "Failed to sync preferences", e)
        }
    }

    // Fetch preferences from Supabase User Metadata
    suspend fun fetchUserPreferences(): Triple<String?, String?, Float?> {
        return try {
            val user = supabase.auth.currentUserOrNull() ?: return Triple(null, null, null)
            val theme = user.userMetadata?.get("pref_theme")?.jsonPrimitive?.contentOrNull
            val accent = user.userMetadata?.get("pref_accent")?.jsonPrimitive?.contentOrNull
            val fontScale = user.userMetadata?.get("pref_font_scale")?.jsonPrimitive?.contentOrNull?.toFloatOrNull()
            Triple(theme, accent, fontScale)
        } catch (e: Exception) {
            Log.e("AuthManager", "Failed to fetch preferences", e)
            Triple(null, null, null)
        }
    }

    // Sign up with email, password, and full name
    suspend fun signUp(email: String, password: String, fullName: String): AuthResult {
        return try {
            val userMetadata = buildJsonObject {
                put("full_name", fullName)
            }
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = userMetadata
            }
            AuthResult.Success
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign up failed", e)
            AuthResult.Error(sanitizeErrorMessage(e.message))
        }
    }

    // Sign in with email and password
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            AuthResult.Success
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign in failed", e)
            AuthResult.Error(sanitizeErrorMessage(e.message))
        }
    }

    // Sign out
    suspend fun signOut(): AuthResult {
        return try {
            supabase.auth.signOut()
            AuthResult.Success
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign out failed", e)
            AuthResult.Error(sanitizeErrorMessage(e.message))
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): AuthResult {
        return try {
            supabase.auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "io.supabase.dailyquoteapp://login-callback"
            )
            AuthResult.Success
        } catch (e: Exception) {
            Log.e("AuthManager", "Password reset failed", e)
            AuthResult.Error(sanitizeErrorMessage(e.message))
        }
    }

    // Helper to clean up the error messages
    private fun sanitizeErrorMessage(rawMessage: String?): String {
        val msg = rawMessage ?: "Unknown error occurred"

        // If the message contains raw HTTP dumps (which usually contain "http" or JSON brackets),
        // return a friendly generic error instead.
        if (msg.contains("Request URL", ignoreCase = true) ||
            msg.contains("http", ignoreCase = true) && msg.length > 100) {

            // Try to guess the context based on common issues
            return if (msg.contains("400") || msg.contains("422")) {
                "Invalid request. Please check your email or password."
            } else if (msg.contains("429")) {
                "Too many requests. Please try again later."
            } else {
                "Network error. Please try again."
            }
        }

        // Handle common auth errors specifically
        if (msg.contains("Email not confirmed", ignoreCase = true)) {
            return "Please verify your email address to login."
        }
        if (msg.contains("Invalid login credentials", ignoreCase = true)) {
            return "Invalid email or password."
        }

        return msg
    }
}

// Sealed class must be outside AuthManager but in the same file to be accessible
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}