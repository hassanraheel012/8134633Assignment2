package com.s8134633.coursenest.ui.dashboard

import androidx.annotation.StringRes
import com.s8134633.coursenest.model.Course

/** Everything the Dashboard screen can show. */
sealed class DashboardUiState {
    data object Loading : DashboardUiState()
    data class Success(val courses: List<Course>) : DashboardUiState()
    data object Empty : DashboardUiState()
    data class Error(@StringRes val messageRes: Int) : DashboardUiState()

    /** No keypass is stored (e.g. the system closed the app, or the user logged out). */
    data object SessionExpired : DashboardUiState()
}
