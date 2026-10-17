package com.s8134633.coursenest.ui.common

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.updatePadding

/**
 * The app draws edge-to-edge so the dashboard header can sit behind the status bar.
 * These helpers keep text and buttons clear of the system bars.
 */
object WindowInsetsHelper {

    /** Adds the status bar height to the view's top padding. */
    fun applyStatusBarPadding(view: View) {
        val initialTop = view.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val statusBarTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = initialTop + statusBarTop)
            insets
        }
        view.doOnAttach { ViewCompat.requestApplyInsets(it) }
    }

    /** Dark status bar icons on cream screens (true), light icons on the dark dashboard header (false). */
    fun setDarkStatusBarIcons(activity: Activity, useDarkIcons: Boolean) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            .isAppearanceLightStatusBars = useDarkIcons
    }
}
