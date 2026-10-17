package com.s8134633.coursenest.data.network

import com.s8134633.coursenest.data.model.DashboardResponse
import com.s8134633.coursenest.data.model.LoginRequest
import com.s8134633.coursenest.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit definition of the nit3213 API endpoints used by the app. */
interface ApiService {

    @POST("$CAMPUS/auth")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponse

    companion object {
        /** Class location used for the auth endpoint. */
        const val CAMPUS = "footscray"
    }
}
