package com.example.group05_flower_api_app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object providing configured Retrofit API client instances.
 */
object ApiClient {

    /**
     * Base URL for the REST API.
     *
     * Deployed Render backend used by the app.
     */
    var baseUrl: String = "https://group-04-flower-api-spring-boot-1.onrender.com/"
        set(value) {
            val formatted = if (value.endsWith("/")) value else "$value/"
            if (field != formatted) {
                field = formatted
                _authApiService = null
            }
        }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private var _authApiService: AuthApiService? = null

    val authApiService: AuthApiService
        get() {
            var instance = _authApiService
            if (instance == null) {
                val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
                instance = Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AuthApiService::class.java)
                _authApiService = instance
            }
            return instance
        }
}
