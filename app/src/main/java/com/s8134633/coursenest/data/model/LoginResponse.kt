package com.s8134633.coursenest.data.model

import com.squareup.moshi.Json

/** Successful login response. The keypass is the topic used by the dashboard endpoint. */
data class LoginResponse(
    @Json(name = "keypass") val keypass: String
)
