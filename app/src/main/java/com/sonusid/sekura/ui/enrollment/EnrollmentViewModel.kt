package com.sonusid.sekura.ui.enrollment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonusid.sekura.domain.model.Account
import com.sonusid.sekura.domain.repository.AccountRepository
import com.sonusid.sekura.util.OtpAuthParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.commons.codec.binary.Base32

data class EnrollmentUiState(
    val issuer: String = "",
    val accountName: String = "",
    val secretKey: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class EnrollmentViewModel(
    private val repository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnrollmentUiState())
    val uiState = _uiState.asStateFlow()

    fun onIssuerChange(value: String) {
        _uiState.update { it.copy(issuer = value, error = null) }
    }

    fun onAccountNameChange(value: String) {
        _uiState.update { it.copy(accountName = value, error = null) }
    }

    fun onSecretKeyChange(value: String) {
        _uiState.update { it.copy(secretKey = value, error = null) }
    }

    fun saveManualAccount() {
        val state = _uiState.value
        if (state.issuer.isBlank() || state.accountName.isBlank() || state.secretKey.isBlank()) {
            _uiState.update { it.copy(error = "All fields are required") }
            return
        }

        if (!isValidBase32(state.secretKey)) {
            _uiState.update { it.copy(error = "Invalid Base32 secret key") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val account = Account(
                issuer = state.issuer,
                accountName = state.accountName,
                secretKey = state.secretKey
            )
            repository.insertAccount(account)
            _uiState.update { it.copy(isSaving = false, isSuccess = true) }
        }
    }

    fun scanQrCode(qrData: String) {
        val account = OtpAuthParser.parse(qrData)
        if (account == null) {
            _uiState.update { it.copy(error = "Invalid QR code format") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.insertAccount(account)
            _uiState.update { it.copy(isSaving = false, isSuccess = true) }
        }
    }

    private fun isValidBase32(secret: String): Boolean {
        return try {
            Base32().decode(secret.uppercase().replace(" ", ""))
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}
