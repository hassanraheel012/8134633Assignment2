package com.s8134633.coursenest.ui.dashboard

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
class DashboardViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    val studentId: String
        get() = sessionManager.studentId.orEmpty()

    val topic: String
        get() = sessionManager.keypass.orEmpty()

    init {
        loadCourses()
    }

    /** Uses the keypass from the login response to load the courses. */
    fun loadCourses() {
        val keypass = sessionManager.keypass
        if (keypass == null) {
            _uiState.value = DashboardUiState.SessionExpired
            return
        }

        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val courses = repository.getCourses(keypass)
                if (courses.isEmpty()) {
                    DashboardUiState.Empty
                } else {
                    DashboardUiState.Success(courses)
                }
            } catch (e: HttpException) {
                DashboardUiState.Error(R.string.error_server)
            } catch (e: IOException) {
                DashboardUiState.Error(R.string.error_network)
            } catch (e: JsonDataException) {
                DashboardUiState.Error(R.string.error_server)
            }
        }
    }

    /** Clears the session; the screen then goes back to Login. */
    fun logout() {
        sessionManager.clear()
        _uiState.value = DashboardUiState.SessionExpired
    }
}
