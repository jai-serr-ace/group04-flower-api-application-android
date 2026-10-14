package com.example.group05_flower_api_app.ui.data.api

/**
 * Data model for authentication response returned by the Spring Boot REST API.
 */
data class AuthResponse(
    val token: String,
    val userId: String,
    val displayName: String,
    val email: String? = null
)
