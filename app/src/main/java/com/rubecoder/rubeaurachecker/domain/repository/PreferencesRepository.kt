package com.rubecoder.rubeaurachecker.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences.
 */
interface PreferencesRepository {
    /**
     * Get whether haptics are enabled.
     */
    suspend fun getEnableHaptics(): Flow<Boolean>

    /**
     * Save whether haptics are enabled.
     */
    suspend fun saveEnableHaptics(enabled: Boolean): Boolean

    /**
     * Get whether sound is enabled.
     */
    suspend fun getEnableSound(): Flow<Boolean>

    /**
     * Save whether sound is enabled.
     */
    suspend fun saveEnableSound(enabled: Boolean): Boolean

    /**
     * Get whether animations are enabled.
     */
    suspend fun getEnableAnimations(): Flow<Boolean>

    /**
     * Save whether animations are enabled.
     */
    suspend fun saveEnableAnimations(enabled: Boolean): Boolean

    /**
     * Clear all preferences (reset to defaults).
     */
    suspend fun clearAllPreferences(): Boolean
}