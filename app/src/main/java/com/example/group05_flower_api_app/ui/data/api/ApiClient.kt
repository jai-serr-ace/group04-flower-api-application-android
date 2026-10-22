package com.example.group05_flower_api_app.ui.data.api

import com.example.group05_flower_api_app.data.api.AuthApiService
import com.example.group05_flower_api_app.data.api.FlowerApiService
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
     * Base URL for the deployed Spring Boot REST API.
     * The trailing slash is required by Retrofit.
     */
    var baseUrl: String = "https://group-04-flower-api-spring-boot-1.onrender.com/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
        }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        // Render may need a few seconds to wake the service from sleep.
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val flowerApiService: FlowerApiService by lazy {
        retrofit.create(FlowerApiService::class.java)
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
