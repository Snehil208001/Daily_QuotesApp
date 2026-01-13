package com.BrewApp.dailyquoteapp.mainui.profilescreen.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.BrewApp.dailyquoteapp.mainui.profilescreen.viewmodel.ProfileState
import com.BrewApp.dailyquoteapp.mainui.profilescreen.viewmodel.ProfileViewModel
import com.BrewApp.dailyquoteapp.ui.theme.InterFont
import com.BrewApp.dailyquoteapp.ui.theme.PlayfairFont
import com.BrewApp.dailyquoteapp.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

// Color definitions specific to the Profile design
private val CreamBg = Color(0xFFFDFCF5)
private val CreamSurface = Color(0xFFF7F5EB)
private val TextPrimary = Color(0xFF1F2937)
private val TextSecondary = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE7E5E4)
private val RedText = Color(0xFFEF4444)
private val RedBgHover = Color(0xFFFEF2F2)
private val RedBorderHover = Color(0xFFFEE2E2)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit, // This can still be used for text edits if needed
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val avatarUrl by viewModel.avatarUrl.collectAsState() // NEW: Observe Avatar
    val isUploading by viewModel.isUploading.collectAsState() // NEW: Observe Upload Status

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // NEW: Image Picker Launcher
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                scope.launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()
                        if (bytes != null) {
                            viewModel.uploadAvatar(bytes)
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to read image")
                    }
                }
            }
        }
    )

    // Load user data when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    // Handle profile state changes
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.LogoutSuccess -> {
                onLogoutClick()
                viewModel.resetState()
            }
            is ProfileState.Error -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Error).message)
                viewModel.resetState()
            }
            is ProfileState.Success -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Success).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = CreamBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Sticky Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CreamBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Transparent, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Profile",
                    fontFamily = PlayfairFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Max width constraint for larger screens
            Column(
                modifier = Modifier
                    .width(448.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                // 1. Profile Header
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Avatar Image
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .shadow(elevation = 10.dp, shape = CircleShape)
                            .background(Color.Gray, CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUrl != null) {
                            // NEW: Load image from URL
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Default placeholder
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }

                        // NEW: Show loader while uploading
                        if (isUploading) {
                            CircularProgressIndicator(
                                color = PrimaryBlue,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    // Edit Badge (Triggers Photo Picker)
                    Box(
                        modifier = Modifier
                            .padding(bottom = 4.dp, end = 4.dp)
                            .size(36.dp)
                            .background(PrimaryBlue, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                // Launch Photo Picker
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name and Email
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fullName ?: "User",
                        fontFamily = PlayfairFont,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = userEmail ?: "Loading...",
                        fontFamily = InterFont,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 2. Stats Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        count = "124",
                        label = "Quotes Saved"
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        count = "8",
                        label = "Collections"
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Menu List
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        label = "Account Settings",
                        onClick = onSettingsClick
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        onClick = onNotificationsClick
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Favorite,
                        label = "Quote Preferences",
                        onClick = onPreferencesClick
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 4. Logout Button
                Surface(
                    onClick = {
                        if (profileState != ProfileState.Loading) {
                            viewModel.logout()
                        }
                    },
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        if (profileState == ProfileState.Loading) {
                            CircularProgressIndicator(
                                color = RedText,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null,
                                tint = RedText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log Out",
                                fontFamily = InterFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedText,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Version
                Text(
                    text = "Version 2.4.0",
                    fontFamily = InterFont,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    count: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(CreamSurface, RoundedCornerShape(16.dp))
            .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = count,
            fontFamily = PlayfairFont,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            fontFamily = InterFont,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Label
            Text(
                text = label,
                fontFamily = InterFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            // Chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}