package com.rubecoder.rubeaurachecker.data.remote

import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import com.rubecoder.rubeaurachecker.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRemoteDataSource @Inject constructor(
    private val supabaseApi: SupabaseApi
) : RegistrationRepository {

    override suspend fun saveRegistration(registration: UserRegistration): Boolean {
        val supabaseReg = SupabaseRegistration(
            full_name = registration.fullName,
            school_name = registration.schoolName,
            roll_number = registration.rollNumber
        )
        val response = supabaseApi.insertRegistration(supabaseReg)
        return response.error == null
    }

    override fun isRegistrationComplete(): Flow<Boolean> {
        return flow {
            val registrations = supabaseApi.getRegistrations().first()
            emit(registrations.isNotEmpty())
        }
    }

    override fun getRegistration(): Flow<UserRegistration?> {
        return flow {
            val registrations = supabaseApi.getRegistrations().first()
            val reg = registrations.firstOrNull()
            if (reg != null) {
                emit(UserRegistration(reg.full_name, reg.school_name, reg.roll_number))
            } else {
                emit(null)
            }
        }
    }
}