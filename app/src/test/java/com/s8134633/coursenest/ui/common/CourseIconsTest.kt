package com.s8134633.coursenest.ui.common

import com.s8134633.coursenest.R
import org.junit.Assert.assertEquals
import org.junit.Test

class CourseIconsTest {

    @Test
    fun `common course fields get a matching icon`() {
        assertEquals(R.drawable.ic_tag, CourseIcons.iconFor("courseCode"))
        assertEquals(R.drawable.ic_course_book, CourseIcons.iconFor("courseName"))
        assertEquals(R.drawable.ic_person, CourseIcons.iconFor("instructor"))
        assertEquals(R.drawable.ic_schedule, CourseIcons.iconFor("duration"))
        assertEquals(R.drawable.ic_calendar, CourseIcons.iconFor("startDate"))
        assertEquals(R.drawable.ic_star, CourseIcons.iconFor("creditPoints"))
        assertEquals(R.drawable.ic_bar_chart, CourseIcons.iconFor("level"))
        assertEquals(R.drawable.ic_location, CourseIcons.iconFor("campus"))
        assertEquals(R.drawable.ic_category, CourseIcons.iconFor("category"))
    }

    @Test
    fun `matching ignores letter case`() {
        assertEquals(R.drawable.ic_person, CourseIcons.iconFor("INSTRUCTOR"))
    }

    @Test
    fun `unknown fields get the info icon`() {
        assertEquals(R.drawable.ic_info, CourseIcons.iconFor("something"))
        assertEquals(R.drawable.ic_info, CourseIcons.iconFor(""))
    }
}
