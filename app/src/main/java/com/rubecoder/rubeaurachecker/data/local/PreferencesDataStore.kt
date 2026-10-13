package com.rubecoder.rubeaurachecker.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.BooleanPrefsKey
import androidx.datastore.preferences.preferencesDataStore
import com.rubecoder.rubeaurachecker.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesDataStore @Inject constructor(
    private val context: Context
) : PreferencesRepository {

    private val Context.datastore by preferencesDataStore(name = "app_prefs")

    private val ENABLE_HAPTICS_KEY = BooleanPrefsKey("enable_haptics")
    private val ENABLE_SOUND_KEY = BooleanPrefsKey("enable_sound")
    private val ENABLE_ANIMATIONS_KEY = BooleanPrefsKey("enable_animations")

    override suspend fun getEnableHaptics(): Flow<Boolean> {
        return context.datastore.data
            .map { preferences ->
                preferences[ENABLE_HAPTICS_KEY] ?: false
            }
    }

    override suspend fun saveEnableHaptics(enabled: Boolean): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences[ENABLE_HAPTICS_KEY] = enabled
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getEnableSound(): Flow<Boolean> {
        return context.datastore.data
            .map { preferences ->
                preferences[ENABLE_SOUND_KEY] ?: false
            }
    }

    override suspend fun saveEnableSound(enabled: Boolean): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences[ENABLE_SOUND_KEY] = enabled
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getEnableAnimations(): Flow<Boolean> {
        return context.datastore.data
            .map { preferences ->
                preferences[ENABLE_ANIMATIONS_KEY] ?: true
            }
    }

    override suspend fun saveEnableAnimations(enabled: Boolean): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences[ENABLE_ANIMATIONS_KEY] = enabled
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearAllPreferences(): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences.clear()
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}