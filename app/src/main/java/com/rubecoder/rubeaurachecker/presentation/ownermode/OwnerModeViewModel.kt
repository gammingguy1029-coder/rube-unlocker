package com.rubecoder.rubeaurachecker.presentation.ownermode

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.model.AdminRegistration
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerModeViewModel @Inject constructor(
    application: Application,
    private val adminRepository: AdminRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // UI State
    private val _uiState = MutableStateFlow(OwnerModeUiState())
    val uiState: StateFlow<OwnerModeUiState> = _uiState.asStateFlow()

    // Internal state
    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _adminToken = MutableStateFlow<AdminToken?>(null)
    val adminToken: StateFlow<AdminToken?> = _adminToken.asStateFlow()

    private val _registrations = MutableStateFlow<List<AdminRegistration>>(emptyList())
    val registrations: StateFlow<List<AdminRegistration>> = _registrations.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSchool = MutableStateFlow<String>("All Schools")
    val selectedSchool: StateFlow<String> = _selectedSchool.asStateFlow()

    init {
        // Get initial token from savedStateHandle if available
        val initialToken = savedStateHandle.get<String>("token") ?: ""
        if (initialToken.isNotEmpty()) {
            _adminToken.value = AdminToken(token = initialToken, expiresAt = System.currentTimeMillis() + 3600000)
            _uiState.update { it.copy(isAuthenticated = true, errorMessage = null) }
        }

        // Load registrations when token changes
        viewModelScope.launch {
            adminToken.distinctUntilChanged { it?.token == it?.token }
                .filter { it != null }
                .flatMapLatest { token ->
                    // When token changes, fetch registrations
                    adminRepository.getAllRegistrations(token!!)
                        .onStart { emit(emptyList()) }
                        .catch { e ->
                            // Error handling
                            emit(emptyList())
                        }
                }
                .collect { registrations ->
                    _registrations.value = registrations
                }
        }

        // Update filtered list when search query or school changes
        viewModelScope.launch {
            val combined = searchQuery.combine(selectedSchool) { query, school ->
                Pair(query, school)
            }
            combined.flatMapLatest { (query, school) ->
                if (query.isNotEmpty()) {
                    adminRepository.searchRegistrations(_adminToken.value?.token ?: "", query)
                } else if (school != "All Schools") {
                    adminRepository.filterRegistrationsBySchool(_adminToken.value?.token ?: "", school)
                } else {
                    adminRepository.getAllRegistrations(_adminToken.value?.token ?: "")
                }
            }
            .collect { registrations ->
                _registrations.value = registrations
            }
        }
    }

    fun onPinEntered(pin: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            try {
                val token = adminRepository.authenticateAdmin(pin)
                if (token != null) {
                    _adminToken.value = token
                    _uiState.update { it.copy(isAuthenticated = true, errorMessage = null) }
                } else {
                    _uiState.update { it.copy(isAuthenticated = false, errorMessage = "Invalid PIN") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAuthenticated = false, errorMessage = "Authentication failed: ${e.message}") }
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSchoolSelected(school: String) {
        _selectedSchool.value = school
    }

    fun onDeleteRegistration(id: String) {
        viewModelScope.launch {
            val token = _adminToken.value?.token ?: return@launch
            try {
                val success = adminRepository.deleteRegistration(token, id)
                if (success) {
                    // Refresh the list
                    _registrations.value = _registrations.value.filter { it.id != id }
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete registration") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Delete failed: ${e.message}") }
            }
        }
    }

    fun onSignOut() {
        _adminToken.value = null
        _uiState.update { it.copy(isAuthenticated = false) }
        _searchQuery.value = ""
        _selectedSchool.value = "All Schools"
    }

    fun onTokenReceived(token: String) {
        _adminToken.value = AdminToken(token = token, expiresAt = System.currentTimeMillis() + 3600000)
        _uiState.update { it.copy(isAuthenticated = true, errorMessage = null) }
    }
}

data class OwnerModeUiState(
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)