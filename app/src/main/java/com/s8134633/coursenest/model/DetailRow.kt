package com.s8134633.coursenest.model

/**
 * A single row on the Details screen.
 *
 * @param key the field name from the API, used to pick a matching icon.
 * @param label the readable label, e.g. "Course Code".
 */
data class DetailRow(
    val key: String,
    val label: String,
    val value: String
)
