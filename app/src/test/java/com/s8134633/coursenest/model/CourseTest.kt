package com.s8134633.coursenest.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseTest {

    private val course = Course(
        properties = linkedMapOf(
            "courseCode" to "NIT3213",
            "courseName" to "Mobile Application Development",
            "instructor" to "Jane Doe",
            "duration" to "12 weeks"
        ),
        description = "Building Android apps."
    )

    @Test
    fun `code comes from the field whose name contains code`() {
        assertEquals("courseCode", course.codeKey)
        assertEquals("NIT3213", course.code)
    }

    @Test
    fun `title comes from the name field, not the code`() {
        assertEquals("Mobile Application Development", course.title)
    }

    @Test
    fun `summary has the other fields and never the code, title or description`() {
        assertEquals(
            mapOf("instructor" to "Jane Doe", "duration" to "12 weeks"),
            course.summaryProperties
        )
        assertTrue(course.summaryProperties.values.none { it == course.description })
    }

    @Test
    fun `course without a code field has no badge and uses the first field as the title`() {
        val noCode = Course(linkedMapOf("subject" to "Maths", "level" to "Beginner"), "-")

        assertNull(noCode.code)
        assertEquals("Maths", noCode.title)
        assertEquals(mapOf("level" to "Beginner"), noCode.summaryProperties)
    }

    @Test
    fun `an empty code value is treated as no code`() {
        val emptyCode = Course(linkedMapOf("code" to "-", "name" to "Physics"), "-")

        assertNull(emptyCode.code)
    }

    @Test
    fun `course without fields has an empty title and summary`() {
        val emptyCourse = Course(emptyMap(), "Only a description")

        assertEquals("", emptyCourse.title)
        assertTrue(emptyCourse.summaryProperties.isEmpty())
    }
}
