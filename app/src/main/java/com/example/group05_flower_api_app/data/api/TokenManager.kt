package com.example.group05_flower_api_app.data.api

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure token manager that stores and retrieves the verified Google ID token
 * using Android's [EncryptedSharedPreferences].
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        val claims = JwtUtils.parseGoogleIdToken(token)
        claims?.pictureUrl?.let { saveUserPhotoUrl(it) }
        claims?.name?.let { if (getUserName().isNullOrBlank()) saveUserName(it) }
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun saveUserPhotoUrl(url: String?) {
        if (url == null) {
            prefs.edit().remove(KEY_USER_PHOTO_URL).apply()
        } else {
            prefs.edit().putString(KEY_USER_PHOTO_URL, url).apply()
        }
    }

    fun getUserPhotoUrl(): String? {
        return prefs.getString(KEY_USER_PHOTO_URL, null)
    }

    fun saveUserStatus(status: String) {
        prefs.edit().putString(KEY_USER_STATUS, status).apply()
    }

    fun getUserStatus(): String {
        return prefs.getString(KEY_USER_STATUS, "Normal") ?: "Normal"
    }

    fun saveSubmissionCount(count: Int) {
        prefs.edit().putInt(KEY_SUBMISSION_COUNT, count).apply()
    }

    fun getSubmissionCount(): Int {
        return prefs.getInt(KEY_SUBMISSION_COUNT, 0)
    }

    fun resetToDeveloperDefaults() {
        clearAuthToken()
        saveUserName("Developer User")
        saveUserStatus("Normal")
        saveSubmissionCount(0)
    }

    fun clearAuthToken() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_PHOTO_URL)
            .remove(KEY_USER_STATUS)
            .remove(KEY_SUBMISSION_COUNT)
            .apply()
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHOTO_URL = "user_photo_url"
        private const val KEY_USER_STATUS = "user_status"
        private const val KEY_SUBMISSION_COUNT = "submission_count"
    }
}
