package com.example.group05_flower_api_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group05_flower_api_app.data.FlowerRepository
import com.example.group05_flower_api_app.ui.data.api.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: FlowerRepository = FlowerRepository(ApiClient.flowerApiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadFlowers(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                searchQuery = search.orEmpty()
            )

            try {
                val response = repository.getFlowers(search = search)
                _uiState.value = _uiState.value.copy(
                    flowers = response.items,
                    isLoading = false,
                    totalItems = response.totalItems
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Unable to load flowers"
                )
            }
        }
    }
}
