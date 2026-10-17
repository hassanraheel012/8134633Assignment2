package com.s8134633.coursenest.data.model

import com.squareup.moshi.Json

/** Body sent to the /footscray/auth endpoint. */
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)
