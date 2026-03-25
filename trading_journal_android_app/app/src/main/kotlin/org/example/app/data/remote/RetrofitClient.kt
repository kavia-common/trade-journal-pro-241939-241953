package org.example.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton Retrofit client for backend API communication.
 * Base URL should be configured via environment/settings.
 */
// PUBLIC_INTERFACE
object RetrofitClient {

    // Default base URL - should be configured through app settings
    // TODO: Set via environment variable or user settings
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3001/"

    private var baseUrl: String = DEFAULT_BASE_URL

    private var retrofit: Retrofit? = null

    /**
     * Get the ApiService instance with current base URL.
     */
    // PUBLIC_INTERFACE
    fun getApiService(): ApiService {
        if (retrofit == null || retrofit?.baseUrl().toString() != baseUrl) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }

    /**
     * Update the base URL for the API.
     */
    // PUBLIC_INTERFACE
    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
        retrofit = null // Reset to rebuild with new URL
    }
}
