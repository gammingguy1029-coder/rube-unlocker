package com.rubecoder.rubeaurachecker.presentation.registration

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.rubecoder.rubeaurachecker.domain.model.UserRegistration
import com.rubecoder.rubeaurachecker.domain.repository.RegistrationRepository
import com.rubecoder.rubeaurachecker.presentation.registration.RegistrationViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
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
class RegistrationViewModelTest {

    @Mock
    private lateinit var registrationRepository: RegistrationRepository

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = RegistrationViewModel(
            registrationRepository = registrationRepository
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanup()
    }

    @Test
    fun `saveRegistration_success_setsRegistrationComplete`() = runTest {
        // Arrange
        val registration = UserRegistration("Test User", "Test School", "12345")
        `when`(registrationRepository.saveRegistration(any())).thenReturn(true)

        // Act
        viewModel.onFullNameChanged(registration.fullName)
        viewModel.onSchoolNameChanged(registration.schoolName)
        viewModel.onRollNumberChanged(registration.rollNumber)
        viewModel.onConsentChanged(true)
        viewModel.onRegisterClicked()

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.isRegistrationComplete }.isRegistrationComplete shouldBe true
    }

    @Test
    fun `saveRegistration_failure_showsError`() = runTest {
        // Arrange
        `when`(registrationRepository.saveRegistration(any())).thenReturn(false)

        // Act
        viewModel.onFullNameChanged("Test User")
        viewModel.onSchoolNameChanged("Test School")
        viewModel.onRollNumberChanged("12345")
        viewModel.onConsentChanged(true)
        viewModel.onRegisterClicked()

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.error != null }.error shouldBe "Failed to save registration"
    }

    @Test
    fun `emptyFields_showErrors`() = runTest {
        // Act
        viewModel.onRegisterClicked()

        // Assert
        viewModel.uiState.first { it.error == "Please give consent to proceed" }.error shouldBe "Please give consent to proceed"
    }
}