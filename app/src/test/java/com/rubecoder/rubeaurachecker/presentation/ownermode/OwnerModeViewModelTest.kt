package com.rubecoder.rubeaurachecker.presentation.ownermode

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import com.rubecoder.rubeaurachecker.presentation.ownermode.OwnerModeViewModel
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
class OwnerModeViewModelTest {

    @Mock
    private lateinit var adminRepository: AdminRepository

    private lateinit var viewModel: OwnerModeViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = OwnerModeViewModel(
            application = ApplicationProvider.getApplicationContext(),
            adminRepository = adminRepository,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanup()
    }

    @Test
    fun `authenticate with valid PIN should set authenticated state`() = runTest {
        // Arrange
        val validToken = AdminToken(token = "valid-token", expiresAt = System.currentTimeMillis() + 3600000)
        `when`(adminRepository.authenticateAdmin("2012")).thenReturn(validToken)

        // Act
        viewModel.onPinEntered("2012")

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.isAuthenticated }.isAuthenticated shouldBe true
    }

    @Test
    fun `authenticate with invalid PIN should show error`() = runTest {
        // Arrange
        `when`(adminRepository.authenticateAdmin("wrong")).thenReturn(null)

        // Act
        viewModel.onPinEntered("wrong")

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { !it.isAuthenticated }.errorMessage shouldBe "Invalid PIN"
    }

    @Test
    fun `should load initial token from savedStateHandle`() = runTest {
        // Arrange
        savedStateHandle.set("token", "initial-token")

        // Act
        viewModel = OwnerModeViewModel(
            application = ApplicationProvider.getApplicationContext(),
            adminRepository = adminRepository,
            savedStateHandle = savedStateHandle
        )

        // Assert
        viewModel.uiState.first { it.isAuthenticated }.isAuthenticated shouldBe true
        viewModel.adminToken.first { it != null }!!.token shouldBe "initial-token"
    }
}