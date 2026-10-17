package com.s8134633.coursenest.util

import com.s8134633.coursenest.TestData
import com.s8134633.coursenest.model.Course
import com.s8134633.coursenest.model.DetailRow
import org.junit.Assert.assertEquals
import org.junit.Test

class CourseMapperTest {

    @Test
    fun `toCourse separates the description from the other fields`() {
        val raw = mapOf("courseCode" to "NIT3213", "courseName" to "Mobile", "description" to "Apps.")

        val course = CourseMapper.toCourse(raw)

        assertEquals(mapOf("courseCode" to "NIT3213", "courseName" to "Mobile"), course.properties)
        assertEquals("Apps.", course.description)
    }

    @Test
    fun `toCourse keeps the order the api sent the fields in`() {
        val raw = linkedMapOf<String, Any?>("b" to "2", "a" to "1", "c" to "3")

        val course = CourseMapper.toCourse(raw)

        assertEquals(listOf("b", "a", "c"), course.properties.keys.toList())
    }

    @Test
    fun `toCourse finds the description key in any letter case`() {
        val course = CourseMapper.toCourse(mapOf("name" to "Maths", "Description" to "Numbers."))

        assertEquals("Numbers.", course.description)
        assertEquals(mapOf("name" to "Maths"), course.properties)
    }

    @Test
    fun `toCourse uses a dash when there is no description`() {
        val course = CourseMapper.toCourse(mapOf("name" to "Maths"))

        assertEquals("-", course.description)
    }

    @Test
    fun `formatValue removes the decimal from whole numbers`() {
        assertEquals("12", CourseMapper.formatValue(12.0))
    }

    @Test
    fun `formatValue keeps real decimals`() {
        assertEquals("4.5", CourseMapper.formatValue(4.5))
    }

    @Test
    fun `formatValue handles null, blank text and lists`() {
        assertEquals("-", CourseMapper.formatValue(null))
        assertEquals("-", CourseMapper.formatValue("  "))
        assertEquals("a, 2", CourseMapper.formatValue(listOf("a", 2.0)))
        assertEquals("true", CourseMapper.formatValue(true))
    }

    @Test
    fun `formatLabel turns camelCase and snake_case keys into readable labels`() {
        assertEquals("Course Code", CourseMapper.formatLabel("courseCode"))
        assertEquals("Credit Points", CourseMapper.formatLabel("creditPoints"))
        assertEquals("Course Name", CourseMapper.formatLabel("course_name"))
        assertEquals("Instructor", CourseMapper.formatLabel("instructor"))
    }

    @Test
    fun `toDetailRows lists every field except the title`() {
        val rows = CourseMapper.toDetailRows(TestData.firstCourse)

        assertEquals(
            listOf(
                DetailRow("courseCode", "Course Code", "NIT3213"),
                DetailRow("instructor", "Instructor", "Jane Doe")
            ),
            rows
        )
    }

    @Test
    fun `cardSummary shows one labelled line per summary field`() {
        val course = Course(
            linkedMapOf("code" to "NIT1", "name" to "Maths", "instructor" to "Jane", "duration" to "6 weeks"),
            "Numbers."
        )

        assertEquals("Instructor: Jane\nDuration: 6 weeks", CourseMapper.cardSummary(course))
    }

    @Test
    fun `cardSummary is empty when there is only a code and a title`() {
        val course = Course(linkedMapOf("code" to "NIT1", "name" to "Maths"), "Numbers.")

        assertEquals("", CourseMapper.cardSummary(course))
    }
}
