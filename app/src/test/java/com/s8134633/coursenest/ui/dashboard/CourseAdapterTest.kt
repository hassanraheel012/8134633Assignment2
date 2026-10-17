package com.s8134633.coursenest.ui.dashboard

import com.s8134633.coursenest.TestData
import com.s8134633.coursenest.model.Course
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CourseAdapterTest {

    private lateinit var adapter: CourseAdapter
    private val mockNavigationFunction: (Course) -> Unit = {}

    @Before
    fun setUp() {
        adapter = spyk(CourseAdapter(navigationFunction = mockNavigationFunction))
        every { adapter.notifyDataSetChanged() } returns Unit
    }

    @Test
    fun `getItemCount is zero before any data is set`() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `getItemCount returns correct size`() {
        // Given
        val dataList = listOf(
            TestData.firstCourse,
            TestData.secondCourse,
            TestData.firstCourse.copy(description = "Another")
        )
        adapter.setData(dataList)

        // When
        val itemCount = adapter.itemCount

        // Then
        assertEquals(3, itemCount)
    }

    @Test
    fun `setData replaces the old data and calls notifyDataSetChanged`() {
        // Given
        adapter.setData(listOf(TestData.firstCourse, TestData.secondCourse))

        // When
        adapter.setData(listOf(TestData.secondCourse))

        // Then
        assertEquals(1, adapter.itemCount)
        verify(exactly = 2) { adapter.notifyDataSetChanged() }
    }
}
