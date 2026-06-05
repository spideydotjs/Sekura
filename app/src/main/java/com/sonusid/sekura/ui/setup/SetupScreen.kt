package com.sonusid.sekura.ui.setup

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    state: SetupUiState,
    onNameChange: (String) -> Unit,
    onAccountSelected: (GoogleSignInAccount?) -> Unit,
    onComplete: () -> Unit,
    onError: (String) -> Unit,
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
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            onAccountSelected(account)
        } catch (e: com.google.android.gms.common.api.ApiException) {
            val message = when (e.statusCode) {
                10 -> "Developer Error (10): Ensure SHA-1 is registered in Google Cloud Console."
                7 -> "Network Error: Check your internet connection."
                12501 -> "Sign-in cancelled by user."
                else -> "Sign-in failed (${e.statusCode}): ${e.localizedMessage}"
            }
            onError(message)
        } catch (e: Exception) {
            onError("Unexpected error: ${e.localizedMessage}")
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Sekura",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your secure 2FA companion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("What should we call you?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

                if (state.googleAccount == null) {
                    Button(
                        onClick = { launcher.launch(googleSignInClient.signInIntent) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Google Account for Backup")
                    }
                } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Account selected",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = state.googleAccount.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onComplete,
                enabled = state.name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                val buttonText = if (state.googleAccount != null) "Get Started" else "Continue without Backup"
                Text(buttonText)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}
