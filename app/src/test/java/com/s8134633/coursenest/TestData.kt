package com.s8134633.coursenest

import com.s8134633.coursenest.model.Course
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Shared sample data for the unit tests. */
object TestData {

    const val STUDENT_ID = "12345678"
    const val PASSWORD = "Hassan"
    const val KEYPASS = "courses"

    val firstCourse = Course(
        properties = linkedMapOf(
            "courseCode" to "NIT3213",
            "courseName" to "Mobile Application Development",
            "instructor" to "Jane Doe"
        ),
        description = "Building Android apps."
    )

    val secondCourse = Course(
        properties = linkedMapOf(
            "courseCode" to "NIT2112",
            "courseName" to "Web Development",
            "instructor" to "John Smith"
        ),
        description = "Building websites."
    )

    fun httpException(code: Int): HttpException = HttpException(
        Response.error<Any>(code, "{}".toResponseBody("application/json".toMediaType()))
    )
}
