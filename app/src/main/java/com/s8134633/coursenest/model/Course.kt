package com.s8134633.coursenest.model

import com.s8134633.coursenest.util.CourseMapper
import java.io.Serializable

/**
 * One course from the dashboard.
 *
 * The API decides the field names, so the course keeps every field (except the description)
 * in the order the API sent them, and works out its code and title from the field names.
 *
 * Serializable so it can be passed to the Details screen as a safe-args argument (L4).
 *
 * @param properties every field of the course except the description.
 * @param description the course's detailed description.
 */
data class Course(
    val properties: Map<String, String>,
    val description: String
) : Serializable {

    /** The first field whose name contains "code" (e.g. "courseCode"), if the API sent one. */
    val codeKey: String?
        get() = properties.keys.firstOrNull { it.contains(CODE_WORD, ignoreCase = true) }

    /** The course code shown in the badge, or null when the course has no code. */
    val code: String?
        get() = codeKey?.let { properties[it] }?.takeIf { it != CourseMapper.EMPTY_VALUE }

    /** The field used as the title: the first "name"/"title" field, otherwise the first other field. */
    val titleKey: String?
        get() = properties.keys.firstOrNull { key ->
            key != codeKey && TITLE_WORDS.any { key.contains(it, ignoreCase = true) }
        } ?: properties.keys.firstOrNull { it != codeKey }

    val title: String
        get() = titleKey?.let { properties[it] } ?: code.orEmpty()

    /** Everything except the code and the title, shown as the summary on the dashboard card. */
    val summaryProperties: Map<String, String>
        get() = properties.filterKeys { it != codeKey && it != titleKey }

    companion object {
        private const val serialVersionUID = 1L
        private const val CODE_WORD = "code"
        private val TITLE_WORDS = listOf("name", "title")
    }
}
