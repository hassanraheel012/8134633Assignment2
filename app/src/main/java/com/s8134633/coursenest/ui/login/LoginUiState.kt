package com.s8134633.coursenest.ui.login

import androidx.annotation.StringRes

/** Everything the Login screen can show. */
sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class Error(@StringRes val messageRes: Int) : LoginUiState()
}
