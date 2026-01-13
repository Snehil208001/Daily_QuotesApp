package com.BrewApp.dailyquoteapp.mainui.preferences.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.BrewApp.dailyquoteapp.mainui.preferences.viewmodel.PreferencesViewModel
import com.BrewApp.dailyquoteapp.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun QuotePreferencesScreen(
    onBackClick: () -> Unit,
    viewModel: PreferencesViewModel = viewModel()
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.toggleNotifications(true)
                scope.launch { snackbarHostState.showSnackbar("Notifications enabled") }
            } else {
                viewModel.toggleNotifications(false)
                scope.launch { snackbarHostState.showSnackbar("Permission denied") }
            }
        }
    )

    // Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute -> viewModel.updateTime(hourOfDay, minute) },
        notificationTime.first,
        notificationTime.second,
        false
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Preferences",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // --- NOTIFICATIONS SECTION ---
            SectionHeader("Notifications")
            SettingsCard {
                // Toggle Switch
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Daily Quote", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        viewModel.toggleNotifications(true)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.toggleNotifications(true)
                                }
                            } else {
                                viewModel.toggleNotifications(false)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }

                // Time Picker
                if (notificationsEnabled) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { timePickerDialog.show() }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Time", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        }
                        val hour = notificationTime.first
                        val minute = notificationTime.second
                        val amPm = if (hour >= 12) "PM" else "AM"
                        val hour12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                        Text(
                            String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- APPEARANCE SECTION ---
            SectionHeader("Appearance")
            SettingsCard {
                // Theme Selector
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Theme", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ThemeOption(label = "System", selected = themeMode == "system", onClick = { viewModel.updateThemeMode("system") })
                        ThemeOption(label = "Light", selected = themeMode == "light", onClick = { viewModel.updateThemeMode("light") })
                        ThemeOption(label = "Dark", selected = themeMode == "dark", onClick = { viewModel.updateThemeMode("dark") })
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))

                // Accent Color Selector
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accent Color", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ColorOption(color = PrimaryBlue, selected = accentColor == "blue", onClick = { viewModel.updateAccentColor("blue") })
                        ColorOption(color = PrimaryGreen, selected = accentColor == "green", onClick = { viewModel.updateAccentColor("green") })
                        ColorOption(color = PrimaryPurple, selected = accentColor == "purple", onClick = { viewModel.updateAccentColor("purple") })
                        ColorOption(color = PrimaryOrange, selected = accentColor == "orange", onClick = { viewModel.updateAccentColor("orange") })
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))

                // Font Size Slider
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TextFields, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Font Size", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${(fontScale * 100).toInt()}%", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = fontScale,
                        onValueChange = { viewModel.updateFontScale(it) },
                        valueRange = 0.8f..1.4f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        content = content
    )
}

@Composable
fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun ColorOption(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}