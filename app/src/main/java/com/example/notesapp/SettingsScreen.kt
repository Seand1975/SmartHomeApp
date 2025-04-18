package com.example.notesapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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

// SharedPreferences to persist settings
const val PREFS_NAME = "SmartHomePrefs"
const val PREF_USER_NAME = "user_name"
const val PREF_USER_EMAIL = "user_email"
const val PREF_APP_COLOR = "app_color"
const val PREF_SECURITY_ALARM = "security_alarm"
const val PREF_NOTIFICATIONS = "notifications"

// Color options (simplified for this example)
val colorOptions = listOf(
    Color(0xFFFFEB3B), // Yellow (default)
    Color(0xFF90CAF9), // Blue
    Color(0xFFA5D6A7)  // Green
)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // State for settings
    var userName by remember {
        mutableStateOf(sharedPreferences.getString(PREF_USER_NAME, "John Doe") ?: "John Doe")
    }
    var userEmail by remember {
        mutableStateOf(sharedPreferences.getString(PREF_USER_EMAIL, "john@someorg.com") ?: "john@someorg.com")
    }
    var selectedColor by remember {
        mutableStateOf(Color(sharedPreferences.getInt(PREF_APP_COLOR, 0xFFFFEB3B.toInt())))
    }
    var securityAlarm by remember {
        mutableStateOf(sharedPreferences.getBoolean(PREF_SECURITY_ALARM, false))
    }
    var notifications by remember {
        mutableStateOf(sharedPreferences.getBoolean(PREF_NOTIFICATIONS, false))
    }

    // State for dialog
    var showUserDialog by remember { mutableStateOf(false) }
    var showColorDialog by remember { mutableStateOf(false) }

    // Update highlightYellow when color changes
    LaunchedEffect(selectedColor) {
        highlightYellow = selectedColor
        sharedPreferences.edit().putInt(PREF_APP_COLOR, selectedColor.value.toInt()).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Settings Section
        Text(
            text = "USER SETTINGS",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showUserDialog = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                tint = highlightYellow,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = userName, style = MaterialTheme.typography.bodyLarge)
                Text(text = userEmail, style = MaterialTheme.typography.bodySmall)
            }
        }

        // App Settings Section
        Text(
            text = "APP SETTINGS",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )

        // App Color
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showColorDialog = true }
                .padding(vertical = 8.dp),
            verticalAlignment  = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "App Color", style = MaterialTheme.typography.bodyLarge)
        }

        // Auto Arm Security Alarm
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Auto Arm Security Alarm", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = securityAlarm,
                onCheckedChange = {
                    securityAlarm = it
                    sharedPreferences.edit().putBoolean(PREF_SECURITY_ALARM, it).apply()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = highlightYellow,
                    checkedTrackColor = highlightYellow.copy(alpha = 0.5f)
                )
            )
        }

        // App Notifications
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "App Notifications", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = notifications,
                onCheckedChange = {
                    notifications = it
                    sharedPreferences.edit().putBoolean(PREF_NOTIFICATIONS, it).apply()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = highlightYellow,
                    checkedTrackColor = highlightYellow.copy(alpha = 0.5f)
                )
            )
        }

        // Placeholder for Voice Assistant and App Permissions
        Text(
            text = "VOICE",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Voice Assistant",
                tint = highlightYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Voice Assistant", style = MaterialTheme.typography.bodyLarge)
        }

        Text(
            text = "NOTIFICATIONS & PERMISSIONS",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "App Permissions",
                tint = highlightYellow,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "App Permissions", style = MaterialTheme.typography.bodyLarge)
        }
    }

    // User Edit Dialog
    if (showUserDialog) {
        UserEditDialog(
            currentName = userName,
            currentEmail = userEmail,
            onDismiss = { showUserDialog = false },
            onSave = { name, email ->
                userName = name
                userEmail = email
                sharedPreferences.edit()
                    .putString(PREF_USER_NAME, name)
                    .putString(PREF_USER_EMAIL, email)
                    .apply()
                showUserDialog = false
            }
        )
    }

    // Color Picker Dialog
    if (showColorDialog) {
        ColorPickerDialog(
            currentColor = selectedColor,
            onDismiss = { showColorDialog = false },
            onColorSelected = { color ->
                selectedColor = color
                showColorDialog = false
            }
        )
    }
}

@Composable
fun UserEditDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit User Info") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, email) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorPickerDialog(
    currentColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select App Color") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colorOptions.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}