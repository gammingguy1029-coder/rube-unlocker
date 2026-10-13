package com.rubecoder.rubeaurachecker.presentation.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.rubecoder.rubeaurachecker.domain.model.ScanResult
import com.rubecoder.rubeaurachecker.domain.repository.ScanRepository
import com.rubecoder.rubeaurachecker.presentation.scanner.ScannerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ScannerViewModelTest {

    @Mock
    private lateinit var scanRepository: ScanRepository

    private lateinit var viewModel: ScannerViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = ScannerViewModel(
            scanRepository = scanRepository
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanup()
    }

    @Test
    fun `startScan_generatesScoreInRange`() = runTest {
        // Arrange
        `when`(scanRepository.generateAuraScore()).thenReturn(900)

        // Act
        viewModel.onStartScan()
        advanceUntilIdle()

        // Assert
        val uiState = viewModel.uiState.first { it.isScanning }
        uiState.isScanning shouldBe true

        // Wait for scan to complete
        val resultState = viewModel.uiState.first { !it.isScanning }
        resultState.scanResult?.score shouldBe 900
        resultState.scanResult?.score shouldBeInRange(850..999)
    }

    @Test
    fun `perfectAura_showsPerfectMessage`() = runTest {
        // Arrange
        `when`(scanRepository.generateAuraScore()).thenReturn(999)

        // Act
        viewModel.onStartScan()
        advanceUntilIdle()

        // Assert
        val resultState = viewModel.uiState.first { !it.isScanning && it.scanResult?.score == 999 }
        resultState.scanResult?.score shouldBe 999
    }

    @Test
    fun `ownerMode_scoreIsNineMillion`() = runTest {
        // Arrange
        `when`(scanRepository.generateAuraScore(isOwnerMode = true)).thenReturn(9000000)

        // Act
        viewModel.onStartScan(true)
        advanceUntilIdle()

        // Assert
        val resultState = viewModel.uiState.first { !it.isScanning && it.scanResult?.score == 9000000 }
        resultState.scanResult?.score shouldBe 9000000
    }

    @Test
    fun `scanAgain_resetsState`() = runTest {
        // Arrange
        `when`(scanRepository.generateAuraScore()).thenReturn(850)

        // Act
        viewModel.onStartScan()
        advanceUntilIdle()
        viewModel.onScanAgain()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first { it.isScanning }
        state.isScanning shouldBe true
    }
}