package com.example.group05_flower_api_app.data.api

import android.util.Base64
import org.json.JSONObject

data class GoogleTokenClaims(
    val name: String? = null,
    val pictureUrl: String? = null,
    val email: String? = null
)

/**
 * Utility to parse claims (including profile photo URL and user name) from a Google ID token (JWT).
 */
object JwtUtils {

    fun parseGoogleIdToken(idToken: String?): GoogleTokenClaims? {
        if (idToken.isNullOrBlank()) return null
        return try {
            val parts = idToken.split(".")
            if (parts.size >= 2) {
                val payloadBytes = Base64.decode(
                    parts[1],
                    Base64.URL_SAFE or Base64.DEFAULT
                )
                val payloadJson = String(payloadBytes, Charsets.UTF_8)
                val json = JSONObject(payloadJson)
                GoogleTokenClaims(
                    name = json.optString("name").takeIf { it.isNotBlank() },
                    pictureUrl = json.optString("picture").takeIf { it.isNotBlank() },
                    email = json.optString("email").takeIf { it.isNotBlank() }
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
