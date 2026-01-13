package com.BrewApp.dailyquoteapp.mainui.loginscreen.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.BrewApp.dailyquoteapp.data.auth.AuthManager
import com.BrewApp.dailyquoteapp.ui.theme.BackgroundCream
import com.BrewApp.dailyquoteapp.ui.theme.InterFont
import com.BrewApp.dailyquoteapp.ui.theme.PlayfairFont
import com.BrewApp.dailyquoteapp.ui.theme.PrimaryBlue
import com.BrewApp.dailyquoteapp.ui.theme.TextPrimary
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

@Composable
fun NewPasswordScreen(
    onPasswordUpdated: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    // We can instantiate AuthManager or access Supabase directly.
    // Since AuthManager wraps Supabase, we'll use it if possible,
    // but modifyUser is a direct Supabase call we implemented earlier.
    // For simplicity here, we use the AuthManager logic.
    val authManager = remember { AuthManager() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .width(420.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Set New Password",
                fontFamily = PlayfairFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Please enter your new password below.",
                fontFamily = InterFont,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password Field
            LoginTextField(
                value = password,
                onValueChange = { password = it },
                label = "New Password",
                placeholder = "Enter new password",
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible }
            )

            // Confirm Password Field
            LoginTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                placeholder = "Re-enter new password",
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = InterFont
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            // Using the direct Supabase call or exposing it via AuthManager
                            // Assuming we updated AuthManager.kt to have modifyUser or similar.
                            // If AuthManager doesn't expose it, we can use the client directly:
                            // SupabaseClient.client.auth.modifyUser { this.password = password }
                            // BUT, based on your AuthManager.kt provided earlier, we fixed `updateUserAvatarUrl`.
                            // We should add a generic updatePassword method or call direct.
                            // Let's call directly for safety in this snippet to avoid errors:
                            com.BrewApp.dailyquoteapp.data.auth.SupabaseClient.client.auth.modifyUser {
                                this.password = password
                            }
                            onPasswordUpdated()
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Update Password",
                        fontFamily = InterFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}