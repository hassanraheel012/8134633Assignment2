package com.s8134633.coursenest.util

import com.s8134633.coursenest.model.Course
import com.s8134633.coursenest.model.DetailRow

/** Converts raw API entities into app models and readable text. */
object CourseMapper {

    const val EMPTY_VALUE = "-"
    private const val DESCRIPTION_KEY = "description"

    /** Splits a raw entity into its description and its other fields. */
    fun toCourse(raw: Map<String, Any?>): Course {
        val descriptionEntry = raw.entries.firstOrNull { isDescriptionKey(it.key) }
        val properties = LinkedHashMap<String, String>()
        raw.forEach { (key, value) ->
            if (!isDescriptionKey(key)) {
                properties[key] = formatValue(value)
            }
        }
        return Course(
            properties = properties,
            description = descriptionEntry?.let { formatValue(it.value) } ?: EMPTY_VALUE
        )
    }

    /** Rows for the Details screen: every field except the title, which is shown in the header. */
    fun toDetailRows(course: Course): List<DetailRow> =
        course.properties
            .filterKeys { it != course.titleKey }
            .map { (key, value) -> DetailRow(key = key, label = formatLabel(key), value = value) }

    /** The dashboard card summary, one "Label: value" line per field. */
    fun cardSummary(course: Course): String =
        course.summaryProperties.entries.joinToString("\n") { (key, value) ->
            "${formatLabel(key)}: $value"
        }

    /** Turns a JSON value into display text, e.g. 1990.0 -> "1990". */
    fun formatValue(value: Any?): String = when (value) {
        null -> EMPTY_VALUE
        is Double -> if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
        is List<*> -> value.joinToString(", ") { formatValue(it) }
        else -> value.toString().ifBlank { EMPTY_VALUE }
    }

    /** Turns a JSON key into a label, e.g. "courseCode" or "course_code" -> "Course Code". */
    fun formatLabel(key: String): String =
        key.replace("_", " ")
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }

    private fun isDescriptionKey(key: String) = key.equals(DESCRIPTION_KEY, ignoreCase = true)
}
