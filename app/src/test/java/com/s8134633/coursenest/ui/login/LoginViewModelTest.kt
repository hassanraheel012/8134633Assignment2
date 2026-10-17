package com.s8134633.coursenest.ui.login

import com.s8134633.coursenest.R
import com.s8134633.coursenest.TestData
import com.s8134633.coursenest.data.repository.CourseRepository
import com.s8134633.coursenest.data.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: CourseRepository = mockk()
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionManager = SessionManager()
        viewModel = LoginViewModel(repository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `empty student id shows an error and does not call the api`() = runTest {
        // When
        viewModel.login("", TestData.PASSWORD)
        advanceUntilIdle()

        // Then
        assertEquals(LoginUiState.Error(R.string.error_empty_fields), viewModel.uiState.value)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `empty password shows an error and does not call the api`() = runTest {
        viewModel.login(TestData.STUDENT_ID, "")
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_empty_fields), viewModel.uiState.value)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `state is Loading while the request is running`() = runTest {
        // Given
        coEvery { repository.login(any(), any()) } returns TestData.KEYPASS

        // When
        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)

        // Then (the coroutine hasn't run yet)
        assertEquals(LoginUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `successful login emits Success and saves the session`() = runTest {
        // Given
        coEvery { repository.login(TestData.STUDENT_ID, TestData.PASSWORD) } returns TestData.KEYPASS

        // When
        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        advanceUntilIdle()

        // Then
        assertEquals(LoginUiState.Success, viewModel.uiState.value)
        assertEquals(TestData.STUDENT_ID, sessionManager.studentId)
        assertEquals(TestData.KEYPASS, sessionManager.keypass)
    }

    @Test
    fun `student id is trimmed but the case sensitive password is sent unchanged`() = runTest {
        coEvery { repository.login(any(), any()) } returns TestData.KEYPASS

        viewModel.login("  ${TestData.STUDENT_ID} ", TestData.PASSWORD)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.login(TestData.STUDENT_ID, TestData.PASSWORD) }
    }

    @Test
    fun `wrong credentials (http 4xx) show the invalid credentials error`() = runTest {
        coEvery { repository.login(any(), any()) } throws TestData.httpException(401)

        viewModel.login(TestData.STUDENT_ID, "wrong")
        advanceUntilIdle()

        assertEquals(
            LoginUiState.Error(R.string.error_invalid_credentials),
            viewModel.uiState.value
        )
        assertNull(sessionManager.keypass)
    }

    @Test
    fun `server error (http 5xx) shows the server error`() = runTest {
        coEvery { repository.login(any(), any()) } throws TestData.httpException(500)

        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_server), viewModel.uiState.value)
    }

    @Test
    fun `no internet shows the network error`() = runTest {
        coEvery { repository.login(any(), any()) } throws IOException("No network")

        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_network), viewModel.uiState.value)
    }

    @Test
    fun `tapping login again while loading only sends one request`() = runTest {
        coEvery { repository.login(any(), any()) } returns TestData.KEYPASS

        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.login(any(), any()) }
    }

    @Test
    fun `onNavigationHandled resets the state to Idle`() = runTest {
        coEvery { repository.login(any(), any()) } returns TestData.KEYPASS
        viewModel.login(TestData.STUDENT_ID, TestData.PASSWORD)
        advanceUntilIdle()

        viewModel.onNavigationHandled()

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }
}
