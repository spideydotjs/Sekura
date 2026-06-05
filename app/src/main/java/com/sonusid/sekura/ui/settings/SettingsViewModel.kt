package com.sonusid.sekura.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sonusid.sekura.data.local.PreferenceManager
import com.sonusid.sekura.data.remote.GoogleDriveManager
import com.sonusid.sekura.domain.model.Account
import com.sonusid.sekura.domain.repository.AccountRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

data class SettingsUiState(
    val userName: String = "",
    val googleAccount: GoogleSignInAccount? = null,
    val isSyncing: Boolean = false,
    val lastSyncTime: String? = null,
    val error: String? = null
)

class SettingsViewModel(
    private val repository: AccountRepository,
    private val googleDriveManager: GoogleDriveManager,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val account = googleDriveManager.getLastSignedInAccount()
        if (account != null) {
            _uiState.update { it.copy(googleAccount = account) }
        }

        viewModelScope.launch {
            combine(
                preferenceManager.userName.filterNotNull(),
                preferenceManager.lastSyncTime
            ) { name, lastSync ->
                name to lastSync
            }.collect { (name, lastSync) ->
                _uiState.update { it.copy(userName = name, lastSyncTime = lastSync) }
            }
        }
    }

    fun onGoogleAccountSelected(account: GoogleSignInAccount?) {
        _uiState.update { it.copy(googleAccount = account) }
        if (account != null) {
            syncWithCloud()
        }
    }

    fun syncWithCloud() {
        val account = _uiState.value.googleAccount ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null) }
            try {
                // 1. Download existing backup
                val cloudJson = googleDriveManager.downloadBackup(account)
                if (cloudJson != null) {
                    val cloudAccounts = Json.decodeFromString<List<Account>>(cloudJson)
                    for (cloudAccount in cloudAccounts) {
                        val localAccount = repository.getAccountById(cloudAccount.id)
                        if (localAccount == null) {
                            repository.insertAccount(cloudAccount)
                        } else {
                            repository.updateAccount(cloudAccount)
                        }
                    }
                }

                // 2. Upload latest local state to cloud
                val allAccounts = repository.getAllAccounts().first()
                val jsonToUpload = Json.encodeToString(allAccounts)
                googleDriveManager.uploadBackup(account, jsonToUpload)

                val currentTime = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())
                preferenceManager.updateLastSyncTime(currentTime)

                _uiState.update { it.copy(isSyncing = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSyncing = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun signOut() {
        _uiState.value = _uiState.value.copy(googleAccount = null)
    }
}
