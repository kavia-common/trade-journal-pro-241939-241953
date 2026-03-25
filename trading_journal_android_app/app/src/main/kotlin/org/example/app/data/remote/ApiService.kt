package org.example.app.data.remote

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit API service interface for the Trading Journal backend.
 * Currently the backend only has a health check endpoint.
 * Additional endpoints will be added as the backend grows.
 */
// PUBLIC_INTERFACE
interface ApiService {

    /**
     * Health check endpoint to verify backend connectivity.
     */
    @GET("/")
    suspend fun healthCheck(): Response<Map<String, Any>>
}
