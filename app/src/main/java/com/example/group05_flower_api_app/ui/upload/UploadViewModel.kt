package com.example.group05_flower_api_app.ui.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group05_flower_api_app.data.api.ApiClient
import com.example.group05_flower_api_app.data.api.FlowerApiService
import com.example.group05_flower_api_app.data.api.FlowerCreateRequest
import com.example.group05_flower_api_app.data.api.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

data class UploadUiState(
    val flowerName: String = "",
    val description: String = "",
    val quote: String = "",
    val tags: String = "",
    val imageUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val uploadedFlowerId: String? = null,
    val errorMessage: String? = null
)

class UploadViewModel(
    context: Context? = null,
    private val apiService: FlowerApiService = ApiClient.flowerApiService,
    private val authTokenProvider: (() -> String?)? = null
) : ViewModel() {

    private val tokenManager = context?.let { TokenManager(it.applicationContext) }

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    fun onFlowerNameChanged(name: String) {
        _uiState.update { it.copy(flowerName = name) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onQuoteChanged(quote: String) {
        _uiState.update { it.copy(quote = quote) }
    }

    fun onTagsChanged(tags: String) {
        _uiState.update { it.copy(tags = tags) }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun clearImage() {
        _uiState.update { it.copy(imageUri = null) }
    }

    private val pendingTags = mutableListOf<String>()

    fun upload() {
        val currentState = _uiState.value
        if (currentState.isUploading || currentState.uploadSuccess) return
        if (currentState.flowerName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Flower name cannot be empty") }
            return
        }

        if (currentState.imageUri != null || currentState.quote.isNotBlank()) {
            _uiState.update { it.copy(errorMessage = "The API does not support image uploads or linking quotes to flowers yet.") }
            return
        }
        val token = (authTokenProvider?.invoke() ?: tokenManager?.getAuthToken())
            ?.takeIf { it.isNotBlank() }
        if (token == null) {
            _uiState.update { it.copy(errorMessage = "Please sign in before uploading a flower.") }
            return
        }
        _uiState.update { it.copy(isUploading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                var flowerId = currentState.uploadedFlowerId
                if (flowerId == null) {
                    val profile = apiService.getCurrentUser(authHeader)
                    val userId = profile.body()?.id?.toLongOrNull()?.takeIf { it > 0 }
                    if (!profile.isSuccessful || userId == null) {
                        _uiState.update { it.copy(isUploading = false,
                            errorMessage = "Could not resolve your database user ID from /users/me. The server must return a userId for your signed-in account.") }
                        return@launch
                    }
                    val request = FlowerCreateRequest(
                        name = currentState.flowerName.trim(),
                        userId = userId,
                        notes = currentState.description.trim().ifBlank { null }
                    )
                    val response = apiService.createFlower(authHeader = authHeader, request = request)
                    if (!response.isSuccessful) {
                        _uiState.update { it.copy(isUploading = false, errorMessage =
                            when (response.code()) {
                                401 -> "Your session expired. Please sign in again."
                                403 -> "You do not have permission to upload flowers."
                                else -> "Could not upload flower (${response.code()}). Please try again."
                            }) }
                        return@launch
                    }
                    flowerId = response.body()?.resolvedId()?.takeIf { it.isNotBlank() }
                    if (flowerId == null) {
                        _uiState.update { it.copy(isUploading = false,
                            errorMessage = "The server accepted the flower but returned no ID. Check your submissions before uploading again.") }
                        return@launch
                    }
                    pendingTags.clear()
                    pendingTags.addAll(currentState.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct())
                    _uiState.update { it.copy(uploadedFlowerId = flowerId) }
                }
                while (pendingTags.isNotEmpty()) {
                    val response = apiService.addTagToFlower(authHeader, flowerId, mapOf("userTag" to pendingTags.first()))
                    if (!response.isSuccessful) {
                        _uiState.update { it.copy(isUploading = false,
                            errorMessage = "Flower saved, but tags could not be saved (${response.code()}). Retry to save the remaining tags.") }
                        return@launch
                    }
                    pendingTags.removeAt(0)
                }
                _uiState.update { it.copy(isUploading = false, uploadSuccess = true) }
            } catch (e: CancellationException) {
                _uiState.update { it.copy(isUploading = false) }
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploading = false, errorMessage =
                    if (it.uploadedFlowerId != null) "Flower saved, but tags could not be saved. Retry to save the remaining tags."
                    else "Could not connect to the flower service. Please try again.") }
            }
        }
    }

    fun resetUploadSuccess() {
        _uiState.update { it.copy(uploadSuccess = false) }
    }
}
