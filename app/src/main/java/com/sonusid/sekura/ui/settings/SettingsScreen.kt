package com.sonusid.sekura.ui.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.services.drive.DriveScopes
import com.google.android.gms.common.api.Scope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onGoogleAccountSelected: (GoogleSignInAccount?) -> Unit,
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                onGoogleAccountSelected(account)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                SettingsCategory(title = "Profile")
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = state.userName,
                    subtitle = "User Name",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            item {
                SettingsCategory(title = "Cloud Sync")
                if (state.googleAccount == null) {
                    SettingsItem(
                        icon = Icons.Default.Sync,
                        title = "Sync with Google Drive",
                        subtitle = "Backup your accounts to your Google account",
                        onClick = {
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Backup Active",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Account: ${state.googleAccount.email}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Status: ${if (state.isSyncing) "Syncing..." else "Up to date"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                "Last synced: ${state.lastSyncTime ?: "Just now"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = onSyncNow, enabled = !state.isSyncing) {
                                    if (state.isSyncing) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text("Sync Now")
                                }
                                TextButton(onClick = {
                                    googleSignInClient.signOut().addOnCompleteListener { onSignOut() }
                                }) {
                                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            item {
                SettingsCategory(title = "Security")
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Biometric Lock",
                    subtitle = "Require fingerprint to open Sekura",
                    trailing = { Switch(checked = false, onCheckedChange = {}) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                SettingsCategory(title = "Appearance")
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Dark Theme",
                    subtitle = "Switch between light and dark themes",
                    trailing = { Switch(checked = true, onCheckedChange = {}) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                SettingsCategory(title = "About")
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = "1.0.0 (Release)",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing?.invoke()
    }
}
