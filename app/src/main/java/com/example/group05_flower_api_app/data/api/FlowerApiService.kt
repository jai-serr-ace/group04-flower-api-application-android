package com.example.group05_flower_api_app.data.api

import retrofit2.Response
import retrofit2.http.*

interface FlowerApiService {

    // 1, 2, 3: Health / Root Endpoints
    @GET("/")
    suspend fun getRoot(): Response<Map<String, Any>>

    @GET("api/v1/health")
    suspend fun getHealth(): Response<Map<String, Any>>

    @GET("actuator/health")
    suspend fun getActuatorHealth(): Response<Map<String, Any>>

    // 4: GET /api/v1/flowers (Public search / list)
    @GET("api/v1/flowers")
    suspend fun getFlowers(
        @Query("name") name: String? = null,
        @Query("scientificName") scientificName: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<FlowerPageResponse>

    // 5: GET /api/v1/flowers/{flowerId} (Public single flower detail)
    @GET("api/v1/flowers/{flowerId}")
    suspend fun getFlowerById(
        @Path("flowerId") flowerId: String
    ): Response<FlowerResponse>

    // 6: POST /api/auth/google
    @POST("api/auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

    // 7: GET /api/v1/users/me (Bearer token)
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authHeader: String? = null
    ): Response<UserProfileResponse>

    // 8: GET /api/v1/quotes (Bearer token)
    @GET("api/v1/quotes")
    suspend fun getQuotes(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<QuoteResponse>>

    // 9: GET /api/auth-debug (Bearer token)
    @GET("api/auth-debug")
    suspend fun getAuthDebug(
        @Header("Authorization") authHeader: String? = null
    ): Response<Map<String, Any>>

    // 10: POST /api/v1/flowers (Bearer token flower upload)
    @POST("api/v1/flowers")
    suspend fun createFlower(
        @Header("Authorization") authHeader: String? = null,
        @Body request: FlowerCreateRequest
    ): Response<FlowerResponse>

    // 11: PATCH /api/v1/flowers/{flowerId} (Owner's bearer token)
    @PATCH("api/v1/flowers/{flowerId}")
    suspend fun updateFlower(
        @Header("Authorization") authHeader: String? = null,
        @Path("flowerId") flowerId: String,
        @Body request: Map<String, Any?>
    ): Response<FlowerResponse>

    // 12: DELETE /api/v1/flowers/{flowerId} (Owner's bearer token)
    @DELETE("api/v1/flowers/{flowerId}")
    suspend fun deleteFlower(
        @Header("Authorization") authHeader: String? = null,
        @Path("flowerId") flowerId: String
    ): Response<Unit>

    // 13: POST /api/v1/tags/{flowerId} (Owner's bearer token)
    @POST("api/v1/tags/{flowerId}")
    suspend fun addTagToFlower(
        @Header("Authorization") authHeader: String? = null,
        @Path("flowerId") flowerId: String,
        @Body tagRequest: Map<String, String>
    ): Response<TagResponse>

    // 14: DELETE /api/v1/tags/{flowerId}/{userTag} (Owner's bearer token)
    @DELETE("api/v1/tags/{flowerId}/{userTag}")
    suspend fun removeTagFromFlower(
        @Header("Authorization") authHeader: String? = null,
        @Path("flowerId") flowerId: String,
        @Path("userTag") userTag: String
    ): Response<FlowerResponse>

    // 15: POST /api/v1/quotes (Bearer token)
    @POST("api/v1/quotes")
    suspend fun createQuote(
        @Header("Authorization") authHeader: String? = null,
        @Body request: QuoteCreateRequest
    ): Response<QuoteResponse>
}
