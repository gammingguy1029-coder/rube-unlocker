package com.rubecoder.rubeaurachecker.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import com.rubecoder.rubeaurachecker.domain.repository.RegistrationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    init {
        loadDraft()
    }

    private fun loadDraft() {
        viewModelScope.launch {
            registrationRepository.getRegistration().collect { registration ->
                _uiState.update { it.copy(
                    fullName = registration?.fullName ?: "",
                    schoolName = registration?.schoolName ?: "",
                    rollNumber = registration?.rollNumber ?: "",
                    isRegistrationComplete = registration != null,
                    isLoading = false
                ) }
            }
        }
    }

    fun onFullNameChanged(fullName: String) {
        _uiState.update { it.copy(fullName = fullName, error = null) }
        saveDraft()
    }

    fun onSchoolNameChanged(schoolName: String) {
        _uiState.update { it.copy(schoolName = schoolName, error = null) }
        saveDraft()
    }

    fun onRollNumberChanged(rollNumber: String) {
        _uiState.update { it.copy(rollNumber = rollNumber, error = null) }
        saveDraft()
    }

    fun onConsentChanged(consentGiven: Boolean) {
        _uiState.update { it.copy(consentGiven = consentGiven, error = null) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value
        if (!state.consentGiven) {
            _uiState.update { it.copy(error = "Please give consent to proceed") }
            return
        }
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(error = "Full name is required") }
            return
        }
        if (state.schoolName.isBlank()) {
            _uiState.update { it.copy(error = "School name is required") }
            return
        }
        if (state.rollNumber.isBlank()) {
            _uiState.update { it.copy(error = "Roll number is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val registration = UserRegistration(
                fullName = state.fullName.trim(),
                schoolName = state.schoolName.trim(),
                rollNumber = state.rollNumber.trim()
            )
            val success = registrationRepository.saveRegistration(registration)
            _uiState.update { it.copy(isLoading = false) }
            if (success) {
                // In a real app, we would navigate to home screen here.
                // For now, we just clear the error and set a success message.
                _uiState.update { it.copy(error = null, isRegistrationComplete = true) }
            } else {
                _uiState.update { it.copy(error = "Failed to save registration") }
            }
        }
    }

    private fun saveDraft() {
        val state = _uiState.value
        viewModelScope.launch {
            val registration = UserRegistration(
                fullName = state.fullName,
                schoolName = state.schoolName,
                rollNumber = state.rollNumber
            )
            registrationRepository.saveRegistration(registration)
        }
    }
}

data class RegistrationUiState(
    val fullName: String = "",
    val schoolName: String = "",
    val rollNumber: String = "",
    val consentGiven: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationComplete: Boolean = false
)