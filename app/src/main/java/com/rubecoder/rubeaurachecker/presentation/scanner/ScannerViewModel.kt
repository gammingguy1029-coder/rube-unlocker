package com.rubecoder.rubeaurachecker.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rubecoder.rubeaurachecker.domain.repository.HistoryRepository
import com.rubecoder.rubeaurachecker.domain.usecase.GenerateAuraScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val generateAuraScoreUseCase: GenerateAuraScoreUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var scanJob: Job? = null

    fun onScanClicked() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            // Reset state
            _uiState.update { it.copy(isScanning = true, progress = 0f, score = null, error = null) }

            try {
                // Simulate scanning progress
                val durationMillis = 3000L
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < durationMillis) {
                    val progress = ((System.currentTimeMillis() - startTime).toFloat() / durationMillis).coerceIn(0f, 1f)
                    _uiState.update { it.copy(progress = progress) }
                    delay(50) // Update every 50ms
                }
                _uiState.update { it.copy(progress = 1f) }

                // Generate score and persist it so it shows up in history
                val score = generateAuraScoreUseCase.invoke()
                historyRepository.addScan(score)
                _uiState.update { it.copy(isScanning = false, score = score) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isScanning = false, error = "Failed to generate score") }
            }
        }
    }

    fun onCancelClicked() {
        scanJob?.cancel()
        scanJob = null
        _uiState.update { it.copy(isScanning = false, progress = 0f, score = null, error = null) }
    }
}

data class ScannerUiState(
    val isScanning: Boolean = false,
    val progress: Float = 0f,
    val score: Int? = null,
    val error: String? = null
)