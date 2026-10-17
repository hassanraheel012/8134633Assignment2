package com.s8134633.coursenest.ui.details

import com.s8134633.coursenest.model.DetailRow
import io.mockk.every
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DetailRowAdapterTest {

    private lateinit var adapter: DetailRowAdapter

    @Before
    fun setUp() {
        adapter = spyk(DetailRowAdapter())
        every { adapter.notifyDataSetChanged() } returns Unit
    }

    @Test
    fun `setData shows one row per field`() {
        adapter.setData(
            listOf(
                DetailRow("courseCode", "Course Code", "NIT3213"),
                DetailRow("instructor", "Instructor", "Jane Doe")
            )
        )

        assertEquals(2, adapter.itemCount)
    }
}
