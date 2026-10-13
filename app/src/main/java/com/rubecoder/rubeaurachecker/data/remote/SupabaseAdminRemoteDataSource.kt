package com.rubecoder.rubeaurachecker.data.remote

import com.rubecoder.rubeaurachecker.domain.model.AdminRegistration
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseAdminRemoteDataSource @Inject constructor(
    private val supabaseAdminApi: SupabaseAdminApi
) : AdminRepository {

    override suspend fun authenticateAdmin(pin: String): AdminToken? {
        val response = supabaseAdminApi.authenticateAdmin(AuthRequest(pin))
        return AdminToken(
            token = response.token,
            expiresAt = response.expiresAt
        )
    }

    override suspend fun getAllRegistrations(token: String): Flow<List<AdminRegistration>> {
        return supabaseAdminApi.getAdminRegistrations("Bearer $token")
            .map { registrations ->
                registrations.map { reg ->
                    AdminRegistration(
                        id = reg.id ?: "",
                        fullName = reg.full_name,
                        schoolName = reg.school_name,
                        rollNumber = reg.roll_number,
                        timestamp = parseTimestamp(reg.created_at)
                    )
                }
            }
    }

    override suspend fun deleteRegistration(token: String, id: String): Boolean {
        val response = supabaseAdminApi.deleteAdminRegistration("Bearer $token", id)
        return response.success && response.error == null
    }

    override suspend fun searchRegistrations(token: String, query: String): Flow<List<AdminRegistration>> {
        return supabaseAdminApi.searchAdminRegistrations("Bearer $token", query)
            .map { registrations ->
                registrations.map { reg ->
                    AdminRegistration(
                        id = reg.id ?: "",
                        fullName = reg.full_name,
                        schoolName = reg.school_name,
                        rollNumber = reg.roll_number,
                        timestamp = parseTimestamp(reg.created_at)
                    )
                }
            }
    }

    override suspend fun filterRegistrationsBySchool(token: String, school: String): Flow<List<AdminRegistration>> {
        return supabaseAdminApi.filterAdminRegistrationsBySchool("Bearer $token", school)
            .map { registrations ->
                registrations.map { reg ->
                    AdminRegistration(
                        id = reg.id ?: "",
                        fullName = reg.full_name,
                        schoolName = reg.school_name,
                        rollNumber = reg.roll_number,
                        timestamp = parseTimestamp(reg.created_at)
                    )
                }
            }
    }

    private fun parseTimestamp(createdAt: String?): Long {
        // Try to parse ISO 8601 timestamp, fallback to current time
        return try {
            if (createdAt != null) {
                java.time.Instant.parse(createdAt).toEpochMilli()
            } else {
                System.currentTimeMillis()
            }
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}