package com.sonusid.sekura.ui.navigation

import androidx.compose.material3.Button
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SekuraNavHost(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Destination.Home as NavKey)
    val context = LocalContext.current
    val repository = (context.applicationContext as SekuraApplication).repository
    val strategy = rememberListDetailSceneStrategy<NavKey>()

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
                is Destination.Home -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.listPane(),
                    content = {
                        HomeRoute(repository, onAddAccount = {
                            backStack.add(Destination.AddAccount)
                        })
                    }
                )
                is Destination.AddAccount -> NavEntry(
                    key = key,
                    metadata = ListDetailSceneStrategy.detailPane(),
                    content = {
                        EnrollmentRoute(repository, onBack = {
                            backStack.removeLastOrNull()
                        })
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
    onBack: () -> Unit
) {
    val viewModel: EnrollmentViewModel = viewModel {
        EnrollmentViewModel(repository)
    }
    val state by viewModel.uiState.collectAsState()
    EnrollmentScreen(
        state = state,
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
    onAddAccount: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel {
        HomeViewModel(repository)
    }
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onAddAccountClick = onAddAccount,
        onDeleteAccount = {
            viewModel.deleteAccount(it)
        }
    )
}

