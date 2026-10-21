package com.example.group05_flower_api_app.ui.user

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.group05_flower_api_app.data.api.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserUiState(
    val photoUrl: String? = null,
    val name: String = "Google User",
    val status: String = "Normal",
    val submissionCount: Int = 0
)

class UserViewModel(context: Context? = null) : ViewModel() {
    private val tokenManager = context?.let { TokenManager(it) }

    private val _uiState = MutableStateFlow(
        UserUiState(
            photoUrl = tokenManager?.getUserPhotoUrl(),
            name = tokenManager?.getUserName()?.takeIf { it.isNotBlank() } ?: "Google User",
            status = tokenManager?.getUserStatus() ?: "Normal",
            submissionCount = tokenManager?.getSubmissionCount() ?: 0
        )
    )
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun toggleStatus() {
        val current = _uiState.value.status
        val newStatus = if (current == "Normal") "Admin" else "Normal"
        _uiState.value = _uiState.value.copy(status = newStatus)
        tokenManager?.saveUserStatus(newStatus)
    }

    fun signOut() {
        tokenManager?.clearAuthToken()
    }
}
