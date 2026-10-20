package com.example.group05_flower_api_app.ui.data.api

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
     * Base URL for the Spring Boot REST API.
     * Default: "http://10.0.2.2:8080/" (Android Emulator localhost pointing to Spring Boot default port 8080).
     *
     * IMPORTANT FOR DEPLOYMENT: Update this URL to your production HTTPS backend domain
     * (e.g., "https://api.yourdomain.com/") before committing production builds.
     */
    var baseUrl: String = "http://10.0.2.2:8080/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
        }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
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
