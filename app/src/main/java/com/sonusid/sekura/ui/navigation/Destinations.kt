package com.sonusid.sekura.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Home : Destination

    @Serializable
    data object Setup : Destination

    @Serializable
    data class AddAccount(val isQrScan: Boolean) : Destination

    @Serializable
    data object Settings : Destination
}
