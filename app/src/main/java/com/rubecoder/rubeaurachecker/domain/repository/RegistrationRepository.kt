package com.rubecoder.rubeaurachecker.domain.repository

import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    /**
     * Saves the user's registration data.
     * @param registration The user's registration data.
     * @return True if the save was successful, false otherwise.
     */
    suspend fun saveRegistration(registration: UserRegistration): Boolean

    /**
     * Checks if the user has completed registration.
     * @return True if registration is complete, false otherwise.
     */
    fun isRegistrationComplete(): Flow<Boolean>

    /**
     * Gets the user's registration data.
     * @return The user's registration data, or null if not registered.
     */
    fun getRegistration(): Flow<UserRegistration?>
}