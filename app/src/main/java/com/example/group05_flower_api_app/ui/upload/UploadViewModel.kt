package com.example.group05_flower_api_app.ui.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UploadUiState(
    val flowerName: String = "",
    val description: String = "",
    val quote: String = "",
    val tags: String = "",
    val imageUri: Uri? = null,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)

class UploadViewModel : ViewModel() {
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

    fun upload() {
        if (_uiState.value.flowerName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Flower name cannot be empty") }
            return
        }
        _uiState.update { it.copy(isUploading = true, errorMessage = null) }
        // Placeholder upload logic
        _uiState.update { it.copy(isUploading = false, uploadSuccess = true) }
    }

    fun resetUploadSuccess() {
        _uiState.update { it.copy(uploadSuccess = false) }
    }
}
