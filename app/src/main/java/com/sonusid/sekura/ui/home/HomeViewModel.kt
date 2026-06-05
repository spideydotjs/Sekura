package com.sonusid.sekura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonusid.sekura.data.local.PreferenceManager
import com.sonusid.sekura.domain.model.Account
import com.sonusid.sekura.domain.repository.AccountRepository
import com.sonusid.sekura.domain.totp.TOTPGenerator
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AccountUiState(
    val account: Account,
    val currentOtp: String
)

data class HomeUiState(
    val userName: String = "",
    val accounts: List<AccountUiState> = emptyList(),
    val remainingSeconds: Int = 30,
    val progress: Float = 1f,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: AccountRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _timerFlow = flow {
        while (true) {
            val remaining = TOTPGenerator.getRemainingSeconds()
            emit(remaining)
            delay(500) // Update every half second for smoothness
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllAccounts(),
        _timerFlow,
        preferenceManager.userName
    ) { accounts, remaining, userName ->
        HomeUiState(
            userName = userName ?: "User",
            accounts = accounts.map { account ->
                AccountUiState(
                    account = account,
                    currentOtp = TOTPGenerator.generateTOTP(account.secretKey)
                )
            },
            remainingSeconds = remaining,
            progress = remaining / 30f,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }
}
