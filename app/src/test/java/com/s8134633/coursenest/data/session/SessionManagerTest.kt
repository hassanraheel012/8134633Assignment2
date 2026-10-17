package com.s8134633.coursenest.data.session

import com.s8134633.coursenest.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerTest {

    private val sessionManager = SessionManager()

    @Test
    fun `nobody is logged in at the start`() {
        assertFalse(sessionManager.isLoggedIn)
        assertNull(sessionManager.keypass)
    }

    @Test
    fun `startSession stores the student id and keypass`() {
        sessionManager.startSession(TestData.STUDENT_ID, TestData.KEYPASS)

        assertTrue(sessionManager.isLoggedIn)
        assertEquals(TestData.STUDENT_ID, sessionManager.studentId)
        assertEquals(TestData.KEYPASS, sessionManager.keypass)
    }

    @Test
    fun `clear removes everything`() {
        sessionManager.startSession(TestData.STUDENT_ID, TestData.KEYPASS)

        sessionManager.clear()

        assertFalse(sessionManager.isLoggedIn)
        assertNull(sessionManager.studentId)
        assertNull(sessionManager.keypass)
    }
}
