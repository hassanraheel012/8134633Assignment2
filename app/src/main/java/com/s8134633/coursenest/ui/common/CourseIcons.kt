package com.s8134633.coursenest.ui.common

import androidx.annotation.DrawableRes
import com.s8134633.coursenest.R
import java.util.Locale

/**
 * Picks an icon for a course field from its name (e.g. "instructor" -> person icon).
 * Fields that don't match anything get a neutral info icon.
 */
object CourseIcons {

    private val personWords = listOf("instructor", "teacher", "lecturer", "tutor", "author", "coordinator")
    private val durationWords = listOf("duration", "hour", "week", "length", "time")
    private val dateWords = listOf("date", "semester", "term", "start", "year")
    private val creditWords = listOf("credit", "point", "rating", "score")
    private val levelWords = listOf("level", "difficulty")
    private val locationWords = listOf("location", "campus", "delivery", "mode", "room")
    private val categoryWords = listOf("category", "subject", "field", "department", "faculty", "type")
    private val titleWords = listOf("name", "title", "course")

    @DrawableRes
    fun iconFor(fieldName: String): Int {
        val name = fieldName.lowercase(Locale.ROOT)
        return when {
            "code" in name -> R.drawable.ic_tag
            personWords.any { it in name } -> R.drawable.ic_person
            durationWords.any { it in name } -> R.drawable.ic_schedule
            dateWords.any { it in name } -> R.drawable.ic_calendar
            creditWords.any { it in name } -> R.drawable.ic_star
            levelWords.any { it in name } -> R.drawable.ic_bar_chart
            locationWords.any { it in name } -> R.drawable.ic_location
            categoryWords.any { it in name } -> R.drawable.ic_category
            titleWords.any { it in name } -> R.drawable.ic_course_book
            else -> R.drawable.ic_info
        }
    }
}
