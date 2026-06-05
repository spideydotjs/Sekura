package com.sonusid.sekura.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sonusid.sekura.data.local.PreferenceManager
import com.sonusid.sekura.data.remote.GoogleDriveManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SetupUiState(
    val name: String = "",
    val googleAccount: GoogleSignInAccount? = null,
    val isSetupComplete: Boolean = false,
    val error: String? = null
)

class SetupViewModel(
    private val preferenceManager: PreferenceManager,
    private val googleDriveManager: GoogleDriveManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        val account = googleDriveManager.getLastSignedInAccount()
        if (account != null) {
            _uiState.value = _uiState.value.copy(googleAccount = account)
        }
    }

    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onAccountSelected(account: GoogleSignInAccount?) {
        _uiState.value = _uiState.value.copy(googleAccount = account, error = null)
    }

    fun onError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun completeSetup() {
        viewModelScope.launch {
            preferenceManager.saveUserName(_uiState.value.name)
            preferenceManager.completeSetup()
            _uiState.value = _uiState.value.copy(isSetupComplete = true)
        }
    }
}
