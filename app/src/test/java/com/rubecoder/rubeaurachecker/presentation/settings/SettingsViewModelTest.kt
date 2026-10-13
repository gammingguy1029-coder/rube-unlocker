package com.rubecoder.rubeaurachecker.presentation.settings

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.rubecoder.rubeaurachecker.domain.repository.PreferencesRepository
import com.rubecoder.rubeaurachecker.presentation.settings.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    private lateinit var viewModel: SettingsViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = SettingsViewModel(
            application = ApplicationProvider.getApplicationContext(),
            preferencesRepository = preferencesRepository,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanup()
    }

    @Test
    fun `toggleHaptics_updatesRepositoryAndState`() = runTest {
        // Arrange
        `when`(preferencesRepository.saveEnableHaptics(true)).thenReturn(true)

        // Act
        viewModel.onEnableHapticsChanged(true)
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.enableHaptics }.enableHaptics shouldBe true
    }

    @Test
    fun `toggleSound_updatesRepositoryAndState`() = runTest {
        // Arrange
        `when`(preferencesRepository.saveEnableSound(true)).thenReturn(true)

        // Act
        viewModel.onEnableSoundChanged(true)
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.enableSound }.enableSound shouldBe true
    }

    @Test
    fun `toggleAnimations_updatesRepositoryAndState`() = runTest {
        // Arrange
        `when`(preferencesRepository.saveEnableAnimations(false)).thenReturn(true)

        // Act
        viewModel.onEnableAnimationsChanged(false)
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { !it.enableAnimations }.enableAnimations shouldBe false
    }

    @Test
    fun `resetPreferences_clearsRepositoryAndResetsState`() = runTest {
        // Arrange
        `when`(preferencesRepository.clearAllPreferences()).thenReturn(true)

        // Act
        viewModel.onResetPreferences()
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { !it.enableHaptics && !it.enableSound && it.enableAnimations }
    }
}