package com.s8134633.coursenest.data.model

import com.squareup.moshi.Json

/**
 * Response of /dashboard/{keypass}.
 *
 * The property names inside each entity depend on the topic (keypass),
 * so every entity is read as a Map<String, Any?> instead of a fixed data class.
 */
data class DashboardResponse(
    @Json(name = "entities") val entities: List<Map<String, Any?>> = emptyList(),
    @Json(name = "entityTotal") val entityTotal: Int = 0
)
