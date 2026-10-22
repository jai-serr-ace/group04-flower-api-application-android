package com.example.group05_flower_api_app.ui.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group05_flower_api_app.data.api.ApiClient
import com.example.group05_flower_api_app.data.api.FlowerApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException

data class FlowerDetailUiState(
    val flowerId: String = "",
    val flowerName: String = "",
    val description: String = "",
    val quote: String = "",
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,
    val ownerId: String? = null,
    val createdAt: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ResultViewModel(
    private val apiService: FlowerApiService = ApiClient.flowerApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlowerDetailUiState())
    val uiState: StateFlow<FlowerDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadFlowerDetails(query: String, flowerId: String? = null) {
        loadJob?.cancel()
        val searchQuery = query.trim()
        if (flowerId.isNullOrBlank() && searchQuery.isBlank()) {
            _uiState.value = FlowerDetailUiState(errorMessage = "Enter a flower name to search.")
            return
        }
        _uiState.value = FlowerDetailUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            try {
                val id = flowerId?.takeIf { it.isNotBlank() } ?: run {
                    val search = apiService.getFlowers(name = searchQuery)
                    if (!search.isSuccessful) {
                        _uiState.value = FlowerDetailUiState(
                            errorMessage = "Could not search flowers (${search.code()})."
                        )
                        return@launch
                    }
                    val matches = search.body()?.items.orEmpty()
                    val match = matches.firstOrNull {
                        it.resolvedName().equals(searchQuery, ignoreCase = true)
                    } ?: matches.firstOrNull()
                    match?.resolvedId()?.takeIf { it.isNotBlank() } ?: run {
                        _uiState.value = FlowerDetailUiState(errorMessage = "No flowers found for \"$searchQuery\".")
                        return@launch
                    }
                }
                val response = apiService.getFlowerById(id)
                val flower = response.body()
                if (!response.isSuccessful || flower == null) {
                    _uiState.value = FlowerDetailUiState(errorMessage =
                        if (response.code() == 404) "Flower not found."
                        else "Could not load flower details (${response.code()})."
                    )
                    return@launch
                }
                _uiState.value = FlowerDetailUiState(
                    flowerId = flower.resolvedId(),
                    flowerName = flower.resolvedName(),
                    description = flower.description.orEmpty(),
                    quote = flower.quote.orEmpty(),
                    tags = flower.tags,
                    imageUrl = flower.imageUrl,
                    ownerId = flower.ownerId,
                    createdAt = flower.createdAt
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = FlowerDetailUiState(
                    errorMessage = "Could not connect to the flower service. Please try again."
                )
            }
        }
    }
}
