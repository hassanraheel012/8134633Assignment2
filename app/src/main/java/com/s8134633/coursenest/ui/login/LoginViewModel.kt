package com.s8134633.coursenest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s8134633.coursenest.R
import com.s8134633.coursenest.data.repository.CourseRepository
import com.s8134633.coursenest.data.session.SessionManager
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(studentId: String, password: String) {
        val trimmedStudentId = studentId.trim()
        if (trimmedStudentId.isEmpty() || password.isEmpty()) {
            _uiState.value = LoginUiState.Error(R.string.error_empty_fields)
            return
        }
        if (_uiState.value == LoginUiState.Loading) return

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val keypass = repository.login(trimmedStudentId, password)
                sessionManager.startSession(trimmedStudentId, keypass)
                LoginUiState.Success
            } catch (e: HttpException) {
                LoginUiState.Error(errorMessageFor(e.code()))
            } catch (e: IOException) {
                LoginUiState.Error(R.string.error_network)
            } catch (e: JsonDataException) {
                LoginUiState.Error(R.string.error_server)
            }
        }
    }

    /** Called once the screen has navigated, so going back doesn't navigate again. */
    fun onNavigationHandled() {
        _uiState.value = LoginUiState.Idle
    }

    private fun errorMessageFor(httpCode: Int): Int =
        if (httpCode in 400..499) R.string.error_invalid_credentials else R.string.error_server
}
