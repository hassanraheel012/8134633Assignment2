package com.s8134633.coursenest.data.session

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the logged-in user's details while the app is running.
 *
 * Provided by Hilt as a @Singleton so the Login and Dashboard screens share the same instance.
 */
@Singleton
class SessionManager @Inject constructor() {

    var studentId: String? = null
        private set

    var keypass: String? = null
        private set

    val isLoggedIn: Boolean
        get() = keypass != null

    fun startSession(studentId: String, keypass: String) {
        this.studentId = studentId
        this.keypass = keypass
    }

    fun clear() {
        studentId = null
        keypass = null
    }
}
