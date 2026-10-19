package com.example.group05_flower_api_app.ui.data.api

/** Verified identity returned by POST /api/auth/google. */
data class AuthResponse(
    val subject: String? = null,
    val issuer: String? = null,
    val audience: List<String> = emptyList(),
    val email: String? = null,
    val emailVerified: Boolean? = null
)
