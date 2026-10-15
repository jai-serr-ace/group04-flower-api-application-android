package com.example.group05_flower_api_app.ui.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit REST API interface for Spring Boot authentication endpoints.
 */
interface AuthApiService {

    /**
     * Sends Google OAuth 2.0 ID Token to Spring Boot backend (`POST /api/auth/google`) for verification.
     */
    @POST("api/auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>
}
