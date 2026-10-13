package com.rubecoder.rubeaurachecker.presentation.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val preferencesRepository: PreferencesRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // UI State
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Individual preferences
    private val _enableHaptics = MutableStateFlow(false)
    val enableHaptics: StateFlow<Boolean> = _enableHaptics.asStateFlow()

    private val _enableSound = MutableStateFlow(false)
    val enableSound: StateFlow<Boolean> = _enableSound.asStateFlow()

    private val _enableAnimations = MutableStateFlow(true)
    val enableAnimations: StateFlow<Boolean> = _enableAnimations.asStateFlow()

    init {
        // Load preferences from repository
        viewModelScope.launch {
            preferencesRepository.getEnableHaptics()
                .collect { _enableHaptics.value = it }
            preferencesRepository.getEnableSound()
                .collect { _enableSound.value = it }
            preferencesRepository.getEnableAnimations()
                .collect { _enableAnimations.value = it }
        }

        // Update UI state when any preference changes
        viewModelScope.launch {
            val combined = enableHaptics.combine(enableSound).combine(enableAnimations) { haptics, sound, animations ->
                Triple(haptics, sound, animations)
            }
            combined.collect { (haptics, sound, animations) ->
                _uiState.update { it.copy(
                    enableHaptics = haptics,
                    enableSound = sound,
                    enableAnimations = animations
                ) }
            }
        }
    }

    fun onEnableHapticsChanged(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveEnableHaptics(enabled)
            _enableHaptics.value = enabled
        }
    }

    fun onEnableSoundChanged(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveEnableSound(enabled)
            _enableSound.value = enabled
        }
    }

    fun onEnableAnimationsChanged(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveEnableAnimations(enabled)
            _enableAnimations.value = enabled
        }
    }

    fun onResetPreferences() {
        viewModelScope.launch {
            preferencesRepository.clearAllPreferences()
            // Reset to defaults
            _enableHaptics.value = false
            _enableSound.value = false
            _enableAnimations.value = true
        }
    }
}

data class SettingsUiState(
    val enableHaptics: Boolean = false,
    val enableSound: Boolean = false,
    val enableAnimations: Boolean = true
)