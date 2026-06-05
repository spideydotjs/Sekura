package com.sonusid.sekura.ui.enrollment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.sonusid.sekura.ui.theme.SekuraTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EnrollmentScreen(
    state: EnrollmentUiState,
    onIssuerChange: (String) -> Unit,
    onAccountNameChange: (String) -> Unit,
    onSecretKeyChange: (String) -> Unit,
    onSaveManual: () -> Unit,
    onQrScanned: (String) -> Unit,
    onBack: () -> Unit,
    onResetError: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Account") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0 
                        onResetError()
                    },
                    text = { Text("Scan QR") },
                    icon = { Icon(Icons.Default.QrCodeScanner, null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1 
                        onResetError()
                    },
                    text = { Text("Manual Entry") },
                    icon = { Icon(Icons.Default.Edit, null) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> {
                        if (cameraPermissionState.status.isGranted) {
                            QrScannerView(onCodeScanned = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onQrScanned(it)
                            })
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Camera permission is required to scan QR codes")
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                                    Text("Grant Permission")
                                }
                            }
                        }
                    }
                    1 -> {
                        ManualEntryForm(
                            state = state,
                            onIssuerChange = onIssuerChange,
                            onAccountNameChange = onAccountNameChange,
                            onSecretKeyChange = onSecretKeyChange,
                            onSave = onSaveManual
                        )
                    }
                }
            }

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ManualEntryForm(
    state: EnrollmentUiState,
    onIssuerChange: (String) -> Unit,
    onAccountNameChange: (String) -> Unit,
    onSecretKeyChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = state.issuer,
            onValueChange = onIssuerChange,
            label = { Text("Issuer (e.g. Google)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.accountName,
            onValueChange = onAccountNameChange,
            label = { Text("Account Name (e.g. user@gmail.com)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.secretKey,
            onValueChange = onSecretKeyChange,
            label = { Text("Secret Key") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !state.isSaving,
            shape = MaterialTheme.shapes.large
        ) {
            Text("Save Account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnrollmentScreenPreview() {
    SekuraTheme {
        EnrollmentScreen(
            state = EnrollmentUiState(
                issuer = "Google",
                accountName = "user@gmail.com",
                secretKey = "JBSWY3DPFQQFO33N"
            ),
            onIssuerChange = {},
            onAccountNameChange = {},
            onSecretKeyChange = {},
            onSaveManual = {},
            onQrScanned = {},
            onBack = {},
            onResetError = {}
        )
    }
}
