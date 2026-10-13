package com.rubecoder.rubeaurachecker.presentation.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.model.ScanHistoryItem
import com.rubecoder.rubeaurachecker.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // UI State
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // History items
    private val _historyItems = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val historyItems: StateFlow<List<ScanHistoryItem>> = _historyItems.asStateFlow()

    init {
        // Load history items
        viewModelScope.launch {
            historyRepository.getHistoryItems()
                .collect { items ->
                    _historyItems.value = items
                }
        }
    }

    fun onHistoryItemClicked(item: ScanHistoryItem) {
        // Navigate to result screen with the score from history item
        // This would be handled by the navigation component
        // For now, we just update UI state to show selected item
        _uiState.update { it.copy(selectedItem = item) }
    }

    fun onDeleteHistoryItem(id: String) {
        viewModelScope.launch {
            val success = historyRepository.deleteHistoryItem(id)
            if (success) {
                // Remove from list
                _historyItems.value = _historyItems.value.filter { it.id != id }
                _uiState.update { it.copy(
                    message = "Item deleted",
                    isError = false
                ) }
            } else {
                _uiState.update { it.copy(
                    message = "Failed to delete item",
                    isError = true
                ) }
            }
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            val success = historyRepository.clearHistory()
            if (success) {
                _historyItems.value = emptyList()
                _uiState.update { it.copy(
                    message = "History cleared",
                    isError = false
                ) }
            } else {
                _uiState.update { it.copy(
                    message = "Failed to clear history",
                    isError = true
                ) }
            }
        }
    }
}

data class HistoryUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val isError: Boolean = false,
    val selectedItem: ScanHistoryItem? = null
)