package com.rubecoder.rubeaurachecker.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

@Serializable
data class AuthRequest(
    val pin: String
)

@Serializable
data class AdminTokenResponse(
    val token: String,
    val expiresAt: Long
)

@Serializable
data class AdminRegistrationResponse(
    val id: String?,
    val full_name: String,
    val school_name: String,
    val roll_number: String,
    val created_at: String? = null
)

@Serializable
data class AdminOperationResponse(
    val success: Boolean = true,
    val error: String? = null
)

interface SupabaseAdminApi {
    @POST("admin/auth")
    suspend fun authenticateAdmin(@Body request: AuthRequest): AdminTokenResponse

    @GET("admin/registrations")
    suspend fun getAdminRegistrations(
        @Header("Authorization") authHeader: String
    ): kotlinx.coroutines.flow.Flow<List<AdminRegistrationResponse>>

    @DELETE("admin/registrations/{id}")
    suspend fun deleteAdminRegistration(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): AdminOperationResponse

    @GET("admin/registrations/search")
    suspend fun searchAdminRegistrations(
        @Header("Authorization") authHeader: String,
        @Header("query") query: String
    ): kotlinx.coroutines.flow.Flow<List<AdminRegistrationResponse>>

    @GET("admin/registrations/filter")
    suspend fun filterAdminRegistrationsBySchool(
        @Header("Authorization") authHeader: String,
        @Header("school") school: String
    ): kotlinx.coroutines.flow.Flow<List<AdminRegistrationResponse>>
}