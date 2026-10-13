package com.rubecoder.rubeaurachecker.presentation.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the owner PIN authentication.
 *
 * Note: This PIN-based authentication is client-side only and is not considered strong authentication.
 * It is intended for demonstration purposes only. In a production app, owner authentication would
 * be handled by a secure backend service.
 */
@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState: StateFlow<OwnerUiState> = _uiState.asStateFlow()

    // Submit the PIN for verification
    fun onPinEntered(pin: String) {
        viewModelScope.launch {
            // If we are in a throttled state, ignore
            if (_uiState.value.isThrottled) {
                return@launch
            }

            // Update state to show we are processing
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            // Simulate network delay (even though it's local, we keep UX consistent)
            delay(500)

            // Use admin repository to authenticate against backend
            val token: AdminToken? = try {
                adminRepository.authenticateAdmin(pin)
            } catch (e: Exception) {
                null
            }

            if (token != null) {
                // Correct PIN: set owner mode to true and store token
                _uiState.update { it.copy(
                    isSubmitting = false,
                    isOwnerAuthenticated = true,
                    adminToken = token
                ) }
            } else {
                // Incorrect PIN: increment failed attempts and possibly throttle
                val currentState = _uiState.value
                val newFailedAttempts = currentState.failedAttempts + 1
                val isThrottled = newFailedAttempts >= 3 // Throttle after 3 failed attempts

                _uiState.update { it.copy(
                    isSubmitting = false,
                    failedAttempts = newFailedAttempts,
                    isThrottled = isThrottled,
                    error = if (isThrottled) "Too many attempts. Please try again later." else "Incorrect PIN"
                )}

                // If throttled, we could also set a timer to reset after a period.
                // For simplicity, we'll reset the throttled state after 30 seconds.
                if (isThrottled) {
                    delay(30000) // 30 seconds
                    _uiState.update { it.copy(
                        failedAttempts = 0,
                        isThrottled = false,
                        error = null
                    )}
                }
            }
        }
    }

    // Clear the PIN and reset state (used when navigating away or after successful auth)
    fun clear() {
        _uiState.update { it.copy(
            pin = "",
            isSubmitting = false,
            error = null,
            failedAttempts = 0,
            isThrottled = false,
            isOwnerAuthenticated = false,
            adminToken = null
        )}
    }
}

data class OwnerUiState(
    val pin: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val failedAttempts: Int = 0,
    val isThrottled: Boolean = false,
    val isOwnerAuthenticated: Boolean = false,
    val adminToken: AdminToken? = null
)