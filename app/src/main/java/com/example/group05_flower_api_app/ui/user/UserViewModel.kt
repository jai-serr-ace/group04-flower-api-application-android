package com.example.group05_flower_api_app.ui.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group05_flower_api_app.data.api.ApiClient
import com.example.group05_flower_api_app.data.api.FlowerApiService
import com.example.group05_flower_api_app.data.api.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val userId: String? = null,
    val photoUrl: String? = null,
    val name: String = "Google User",
    val email: String? = null,
    val status: String = "Normal",
    val roles: List<String> = emptyList(),
    val submissionCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UserViewModel(
    context: Context? = null,
    private val apiService: FlowerApiService = ApiClient.flowerApiService
) : ViewModel() {
    private val tokenManager = context?.let { TokenManager(it.applicationContext) }

    private val _uiState = MutableStateFlow(
        UserUiState(
            photoUrl = tokenManager?.getUserPhotoUrl(),
            name = tokenManager?.getUserName()?.takeIf { it.isNotBlank() } ?: "Google User",
            status = tokenManager?.getUserStatus() ?: "Normal",
            submissionCount = tokenManager?.getSubmissionCount() ?: 0,
            isLoading = tokenManager?.getAuthToken() != null
        )
    )
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentUserFromApi()
    }

    private fun fetchCurrentUserFromApi() {
        val token = tokenManager?.getAuthToken() ?: return
        viewModelScope.launch {
            try {
                val response = apiService.getCurrentUser(authHeader = "Bearer $token")
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let { userProfile ->
                        val serverName = userProfile.name?.takeIf { it.isNotBlank() } ?: _uiState.value.name
                        val serverStatus = userProfile.status
                            ?: if (userProfile.roles.contains("ROLE_ADMIN") || userProfile.roles.contains("ADMIN")) "Admin" else _uiState.value.status
                        val serverSubmissions = userProfile.submissionCount
                            ?: userProfile.submissions.size.takeIf { it > 0 } ?: _uiState.value.submissionCount
                        val serverPhoto = userProfile.photoUrl ?: _uiState.value.photoUrl

                        _uiState.value = UserUiState(
                            userId = userProfile.id,
                            photoUrl = serverPhoto,
                            name = serverName,
                            email = userProfile.email,
                            status = serverStatus,
                            roles = userProfile.roles,
                            submissionCount = serverSubmissions,
                            isLoading = false
                        )

                        tokenManager.saveUserName(serverName)
                        tokenManager.saveUserStatus(serverStatus)
                        tokenManager.saveSubmissionCount(serverSubmissions)
                        serverPhoto?.let { tokenManager.saveUserPhotoUrl(it) }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not load your profile (${response.code()})."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Could not connect to the user profile service."
                )
            }
        }
    }

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
