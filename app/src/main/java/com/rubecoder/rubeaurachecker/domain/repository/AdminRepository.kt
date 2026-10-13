package com.rubecoder.rubeaurachecker.domain.repository

import com.rubecoder.rubeaurachecker.domain.model.AdminRegistration
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for admin operations.
 * These operations require backend-controlled authorization (not just client-side PIN).
 */
interface AdminRepository {
    /**
     * Authenticate as admin using a secure backend endpoint.
     * The backend validates the PIN and returns a short-lived admin token (JWT).
     */
    suspend fun authenticateAdmin(pin: String): AdminToken?

    /**
     * Get all registrations (requires valid admin token).
     */
    suspend fun getAllRegistrations(token: String): Flow<List<AdminRegistration>>

    /**
     * Delete a registration by ID (requires valid admin token).
     */
    suspend fun deleteRegistration(token: String, id: String): Boolean

    /**
     * Search registrations by name or school (requires valid admin token).
     */
    suspend fun searchRegistrations(token: String, query: String): Flow<List<AdminRegistration>>

    /**
     * Filter registrations by school (requires valid admin token).
     */
    suspend fun filterRegistrationsBySchool(token: String, school: String): Flow<List<AdminRegistration>>
}