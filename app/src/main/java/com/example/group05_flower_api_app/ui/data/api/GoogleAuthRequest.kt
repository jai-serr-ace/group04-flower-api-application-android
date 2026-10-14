package com.example.group05_flower_api_app.ui.data.api

/**
 * Data model for sending Google ID token to the Spring Boot REST API.
 */
data class GoogleAuthRequest(
    val idToken: String
)
