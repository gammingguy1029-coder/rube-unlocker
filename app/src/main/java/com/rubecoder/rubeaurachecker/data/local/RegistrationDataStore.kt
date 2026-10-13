package com.rubecoder.rubeaurachecker.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetKey
import androidx.datastore.preferences.core.stringKey
import androidx.datastore.preferences.preferencesDataStore
import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import com.rubecoder.rubeaurachecker.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationDataStore @Inject constructor(
    private val context: Context
) : RegistrationRepository {

    private val Context.datastore by preferencesDataStore(name = "registration_prefs")

    private val FULL_NAME_KEY = stringKey("full_name")
    private val SCHOOL_NAME_KEY = stringKey("school_name")
    private val ROLL_NUMBER_KEY = stringKey("roll_number")
    private val IS_REGISTRATION_COMPLETE_KEY = stringKey("is_registration_complete")

    override suspend fun saveRegistration(registration: UserRegistration): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences[FULL_NAME_KEY] = registration.fullName
                preferences[SCHOOL_NAME_KEY] = registration.schoolName
                preferences[ROLL_NUMBER_KEY] = registration.rollNumber
                preferences[IS_REGISTRATION_COMPLETE_KEY] = true
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun isRegistrationComplete(): Flow<Boolean> {
        return context.datastore.data
            .map { preferences ->
                preferences[IS_REGISTRATION_COMPLETE_KEY] ?: false
            }
    }

    override fun getRegistration(): Flow<UserRegistration?> {
        return context.datastore.data
            .map { preferences ->
                val fullName = preferences[FULL_NAME_KEY]
                val schoolName = preferences[SCHOOL_NAME_KEY]
                val rollNumber = preferences[ROLL_NUMBER_KEY]
                val isComplete = preferences[IS_REGISTRATION_COMPLETE_KEY] ?: false
                if (isComplete && fullName != null && schoolName != null && rollNumber != null) {
                    UserRegistration(fullName, schoolName, rollNumber)
                } else {
                    null
                }
            }
    }
}