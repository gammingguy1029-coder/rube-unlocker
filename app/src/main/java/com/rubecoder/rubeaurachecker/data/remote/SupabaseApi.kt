package com.rubecoder.rubeaurachecker.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
data class SupabaseRegistration(
    val id: String? = null,
    val full_name: String,
    val school_name: String,
    val roll_number: String,
    val created_at: String? = null
)

interface SupabaseApi {
    @POST("rest/v1/registrations")
    suspend fun insertRegistration(@Body registration: SupabaseRegistration): SupabaseResponse

    @GET("rest/v1/registrations")
    suspend fun getRegistrations(): Flow<List<SupabaseRegistration>>

    @DELETE("rest/v1/registrations?id=eq.{id}")
    suspend fun deleteRegistration(@Path("id") id: String): SupabaseResponse
}

@Serializable
data class SupabaseResponse(
    val id: String? = null,
    val error: String? = null
)