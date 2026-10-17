package com.s8134633.coursenest.data.repository

import com.s8134633.coursenest.data.model.LoginRequest
import com.s8134633.coursenest.data.network.ApiService
import com.s8134633.coursenest.model.Course
import com.s8134633.coursenest.util.CourseMapper
import javax.inject.Inject

/**
 * Single source of data for the app (L5 repository pattern).
 * The ApiService is injected by Hilt instead of being created here (L7).
 */
class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {

    /** Logs in and returns the keypass. Throws HttpException for wrong credentials. */
    suspend fun login(username: String, password: String): String {
        return apiService.login(LoginRequest(username, password)).keypass
    }

    /** Loads the courses for the given keypass. */
    suspend fun getCourses(keypass: String): List<Course> {
        return apiService.getDashboard(keypass).entities.map { CourseMapper.toCourse(it) }
    }
}
