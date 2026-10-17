package com.s8134633.coursenest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base application class. @HiltAndroidApp triggers Hilt's code generation and
 * makes this the root of the app's dependency graph.
 */
@HiltAndroidApp
class CourseNestApplication : Application()
