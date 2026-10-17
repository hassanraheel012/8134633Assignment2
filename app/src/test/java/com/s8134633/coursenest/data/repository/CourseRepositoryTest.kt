package com.s8134633.coursenest.data.repository

import com.s8134633.coursenest.TestData
import com.s8134633.coursenest.data.model.DashboardResponse
import com.s8134633.coursenest.data.model.LoginRequest
import com.s8134633.coursenest.data.model.LoginResponse
import com.s8134633.coursenest.data.network.ApiService
import com.s8134633.coursenest.model.Course
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class CourseRepositoryTest {

    private val apiService: ApiService = mockk()
    private lateinit var repository: CourseRepository

    @Before
    fun setUp() {
        repository = CourseRepository(apiService)
    }

    @Test
    fun `login sends the student id and password and returns the keypass`() = runTest {
        // Given
        val request = LoginRequest(TestData.STUDENT_ID, TestData.PASSWORD)
        coEvery { apiService.login(request) } returns LoginResponse(TestData.KEYPASS)

        // When
        val keypass = repository.login(TestData.STUDENT_ID, TestData.PASSWORD)

        // Then
        assertEquals(TestData.KEYPASS, keypass)
        coVerify(exactly = 1) { apiService.login(request) }
    }

    @Test
    fun `login passes http errors on to the caller`() = runTest {
        coEvery { apiService.login(any()) } throws TestData.httpException(401)

        val error = runCatching { repository.login(TestData.STUDENT_ID, "wrong") }.exceptionOrNull()

        assertTrue(error is HttpException)
    }

    @Test
    fun `getCourses requests the keypass and maps each entity`() = runTest {
        // Given
        val response = DashboardResponse(
            entities = listOf(
                mapOf("courseCode" to "NIT3213", "courseName" to "Mobile", "description" to "Apps."),
                mapOf("courseCode" to "NIT2112", "credits" to 12.0, "description" to "Web.")
            ),
            entityTotal = 2
        )
        coEvery { apiService.getDashboard(TestData.KEYPASS) } returns response

        // When
        val courses = repository.getCourses(TestData.KEYPASS)

        // Then
        assertEquals(
            listOf(
                Course(linkedMapOf("courseCode" to "NIT3213", "courseName" to "Mobile"), "Apps."),
                Course(linkedMapOf("courseCode" to "NIT2112", "credits" to "12"), "Web.")
            ),
            courses
        )
        coVerify(exactly = 1) { apiService.getDashboard(TestData.KEYPASS) }
    }

    @Test
    fun `getCourses returns an empty list when the api has no entities`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns DashboardResponse(emptyList(), 0)

        assertTrue(repository.getCourses(TestData.KEYPASS).isEmpty())
    }
}
