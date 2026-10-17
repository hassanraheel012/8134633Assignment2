package com.s8134633.coursenest.ui.dashboard

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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: CourseRepository = mockk()
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionManager = SessionManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun loggedInViewModel(): DashboardViewModel {
        sessionManager.startSession(TestData.STUDENT_ID, TestData.KEYPASS)
        return DashboardViewModel(repository, sessionManager)
    }

    @Test
    fun `courses are loaded with the keypass from login`() = runTest {
        // Given
        val courses = listOf(TestData.firstCourse, TestData.secondCourse)
        coEvery { repository.getCourses(TestData.KEYPASS) } returns courses

        // When
        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(DashboardUiState.Success(courses), viewModel.uiState.value)
        coVerify(exactly = 1) { repository.getCourses(TestData.KEYPASS) }
    }

    @Test
    fun `state is Loading before the api responds`() = runTest {
        coEvery { repository.getCourses(any()) } returns listOf(TestData.firstCourse)

        val viewModel = loggedInViewModel()

        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `empty list shows the Empty state`() = runTest {
        coEvery { repository.getCourses(any()) } returns emptyList()

        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        assertEquals(DashboardUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `no internet shows the network error`() = runTest {
        coEvery { repository.getCourses(any()) } throws IOException("No network")

        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        assertEquals(DashboardUiState.Error(R.string.error_network), viewModel.uiState.value)
    }

    @Test
    fun `http error shows the server error`() = runTest {
        coEvery { repository.getCourses(any()) } throws TestData.httpException(404)

        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        assertEquals(DashboardUiState.Error(R.string.error_server), viewModel.uiState.value)
    }

    @Test
    fun `retry after an error loads the courses`() = runTest {
        // Given the first request fails and the second succeeds
        coEvery { repository.getCourses(any()) } throws IOException("No network") andThen
            listOf(TestData.firstCourse)
        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadCourses()
        advanceUntilIdle()

        // Then
        assertEquals(
            DashboardUiState.Success(listOf(TestData.firstCourse)),
            viewModel.uiState.value
        )
        coVerify(exactly = 2) { repository.getCourses(TestData.KEYPASS) }
    }

    @Test
    fun `missing keypass shows SessionExpired and does not call the api`() = runTest {
        // Given nobody has logged in
        val viewModel = DashboardViewModel(repository, sessionManager)
        advanceUntilIdle()

        assertEquals(DashboardUiState.SessionExpired, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.getCourses(any()) }
    }

    @Test
    fun `student id and topic come from the session`() = runTest {
        coEvery { repository.getCourses(any()) } returns emptyList()

        val viewModel = loggedInViewModel()

        assertEquals(TestData.STUDENT_ID, viewModel.studentId)
        assertEquals(TestData.KEYPASS, viewModel.topic)
    }

    @Test
    fun `logout clears the session and returns to login`() = runTest {
        coEvery { repository.getCourses(any()) } returns listOf(TestData.firstCourse)
        val viewModel = loggedInViewModel()
        advanceUntilIdle()

        viewModel.logout()

        assertFalse(sessionManager.isLoggedIn)
        assertEquals(DashboardUiState.SessionExpired, viewModel.uiState.value)
    }
}
