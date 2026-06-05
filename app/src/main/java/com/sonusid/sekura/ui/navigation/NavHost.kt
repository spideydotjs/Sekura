package com.sonusid.sekura.ui.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.rememberNavBackStack
import com.sonusid.sekura.SekuraApplication
import com.sonusid.sekura.ui.home.HomeScreen
import com.sonusid.sekura.ui.home.HomeViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.sonusid.sekura.domain.repository.AccountRepository
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi

import androidx.navigation3.runtime.NavKey

import com.sonusid.sekura.ui.enrollment.EnrollmentScreen
import com.sonusid.sekura.ui.enrollment.EnrollmentViewModel
import com.sonusid.sekura.ui.settings.SettingsScreen
import com.sonusid.sekura.ui.settings.SettingsViewModel
import com.sonusid.sekura.data.remote.GoogleDriveManager
import com.sonusid.sekura.data.local.PreferenceManager
import com.sonusid.sekura.ui.setup.SetupScreen
import com.sonusid.sekura.ui.setup.SetupViewModel
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SekuraNavHost(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as SekuraApplication
    val preferenceManager = app.preferenceManager
    val repository = app.repository
    val googleDriveManager = app.googleDriveManager

    val isSetupComplete by preferenceManager.isSetupComplete.collectAsState(initial = null)

    // We use a placeholder first, and then decide based on setup status
    val backStack = rememberNavBackStack(Destination.Home as NavKey)
    val strategy = rememberListDetailSceneStrategy<NavKey>()

    LaunchedEffect(isSetupComplete) {
        if (isSetupComplete == false) {
            backStack.add(Destination.Setup)
        }
    }

    if (isSetupComplete == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavDisplay<NavKey>(
        backStack = backStack,
        modifier = modifier,
        sceneStrategies = listOf(strategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Destination.Setup -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.listPane(),
                    content = {
                        val viewModel: SetupViewModel = viewModel {
                            SetupViewModel(preferenceManager, googleDriveManager)
                        }
                        val state by viewModel.uiState.collectAsState()
                        SetupScreen(
                            state = state,
                            onNameChange = viewModel::onNameChange,
                            onAccountSelected = viewModel::onAccountSelected,
                            onComplete = {
                                viewModel.completeSetup()
                                backStack.removeLastOrNull()
                            },
                            onError = viewModel::onError
                        )
                    }
                )
                is Destination.Home -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.listPane(),
                    content = {
                        HomeRoute(
                            repository,
                            preferenceManager,
                            onAddAccount = { isQr ->
                                backStack.add(Destination.AddAccount(isQr))
                            },
                            onSettingsClick = {
                                backStack.add(Destination.Settings)
                            }
                        )
                    }
                )
                is Destination.AddAccount -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.detailPane(),
                    content = {
                        EnrollmentRoute(
                            repository = repository,
                            isQrScan = key.isQrScan,
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                )
                is Destination.Settings -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.detailPane(),
                    content = {
                        SettingsRoute(
                            repository,
                            googleDriveManager,
                            preferenceManager,
                            onBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                )
                else -> NavEntry(key) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Unknown Destination")
                    }
                }
            }
        }
    )
}

@Composable
fun EnrollmentRoute(
    repository: AccountRepository,
    isQrScan: Boolean,
    onBack: () -> Unit
) {
    val viewModel: EnrollmentViewModel = viewModel {
        EnrollmentViewModel(repository)
    }
    val state by viewModel.uiState.collectAsState()
    EnrollmentScreen(
        state = state,
        isQrScan = isQrScan,
        onIssuerChange = viewModel::onIssuerChange,
        onAccountNameChange = viewModel::onAccountNameChange,
        onSecretKeyChange = viewModel::onSecretKeyChange,
        onSaveManual = viewModel::saveManualAccount,
        onQrScanned = viewModel::scanQrCode,
        onBack = onBack,
        onResetError = viewModel::resetError
    )
}

@Composable
fun HomeRoute(
    repository: AccountRepository,
    preferenceManager: PreferenceManager,
    onAddAccount: (Boolean) -> Unit,
    onSettingsClick: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel {
        HomeViewModel(repository, preferenceManager)
    }
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onAddAccountClick = onAddAccount,
        onSettingsClick = onSettingsClick,
        onDeleteAccount = {
            viewModel.deleteAccount(it)
        }
    )
}

@Composable
fun SettingsRoute(
    repository: AccountRepository,
    googleDriveManager: GoogleDriveManager,
    preferenceManager: PreferenceManager,
    onBack: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel {
        SettingsViewModel(repository, googleDriveManager, preferenceManager)
    }
    val state by viewModel.uiState.collectAsState()
    SettingsScreen(
        state = state,
        onGoogleAccountSelected = viewModel::onGoogleAccountSelected,
        onSignOut = viewModel::signOut,
        onSyncNow = viewModel::syncWithCloud,
        onBack = onBack
    )
}

