package com.example.group05_flower_api_app.data.api

import com.google.gson.annotations.SerializedName

/** Request payload for creating a new flower via POST /api/v1/flowers */
data class FlowerCreateRequest(
    val name: String,
    val userId: Long,
    val notes: String? = null,
    val scientificName: String? = null,
    val locationCoord: String? = null
)

data class FlowerPageResponse(
    val items: List<FlowerResponse> = emptyList(),
    val page: Int = 0,
    val size: Int = 20,
    val totalItems: Long = 0,
    val totalPages: Int = 0
)

data class TagResponse(val flowerId: String, val userTag: String)

/** Response object representing a flower entity returned by GET or POST /api/v1/flowers */
data class FlowerResponse(
    val id: String? = null,
    val flowerId: String? = id,
    val name: String? = null,
    val flowerName: String? = name,
    @SerializedName(value = "notes", alternate = ["description"])
    val description: String? = null,
    val quote: String? = null,
    val tags: List<String> = emptyList(),
    val imageUrl: String? = null,
    @SerializedName(value = "userId", alternate = ["ownerId"])
    val ownerId: String? = null,
    val createdAt: String? = null
) {
    fun resolvedName(): String = flowerName?.ifBlank { null } ?: name ?: "Flower"
    fun resolvedId(): String = flowerId?.ifBlank { null } ?: id ?: ""
}

/** User profile information returned by GET /api/v1/users/me */
data class UserProfileResponse(
    @SerializedName(value = "userId", alternate = ["id"])
    val id: String? = null,
    @SerializedName(value = "username", alternate = ["name"])
    val name: String? = null,
    val email: String? = null,
    val roles: List<String> = emptyList(),
    val status: String? = null,
    val photoUrl: String? = null,
    val submissionCount: Int? = null,
    val submissions: List<FlowerResponse> = emptyList()
)

/** Quote data model returned or sent via /api/v1/quotes */
data class QuoteResponse(
    val id: String? = null,
    val quote: String? = null,
    val author: String? = null,
    val flowerId: String? = null
)

data class QuoteCreateRequest(
    val quote: String,
    val author: String? = null,
    val flowerId: String? = null
)
