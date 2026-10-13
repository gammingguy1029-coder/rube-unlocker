package com.rubecoder.rubeaurachecker.presentation.owner

import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.rubecoder.rubeaurachecker.domain.model.AdminToken
import com.rubecoder.rubeaurachecker.domain.repository.AdminRepository
import com.rubecoder.rubeaurachecker.presentation.owner.OwnerViewModel
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
class OwnerViewModelTest {

    @Mock
    private lateinit var adminRepository: AdminRepository

    private lateinit var viewModel: OwnerViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = OwnerViewModel(
            adminRepository = adminRepository
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanup()
    }

    @Test
    fun `valid PIN sets isOwnerAuthenticated`() = runTest {
        // Arrange
        val validToken = AdminToken(token = "valid-token", expiresAt = System.currentTimeMillis() + 3600000)
        `when`(adminRepository.authenticateAdmin("2012")).thenReturn(validToken)

        // Act
        viewModel.onPinEntered("2012")

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.isOwnerAuthenticated }.isOwnerAuthenticated shouldBe true
    }

    @Test
    fun `invalid PIN shows error and increments failed attempts`() = runTest {
        // Arrange
        `when`(adminRepository.authenticateAdmin("wrong")).thenReturn(null)

        // Act
        viewModel.onPinEntered("wrong")

        // Advance time for any delayed operations
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.failedAttempts > 0 }.failedAttempts shouldBe 1
        viewModel.uiState.first { it.error != null }.error shouldBe "Incorrect PIN"
    }

    @Test
    fun `throttled after three failed attempts`() = runTest {
        // Arrange
        `when`(adminRepository.authenticateAdmin("wrong")).thenReturn(null)

        // Act
        viewModel.onPinEntered("wrong")
        advanceUntilIdle()
        viewModel.onPinEntered("wrong")
        advanceUntilIdle()
        viewModel.onPinEntered("wrong")
        advanceUntilIdle()

        // Assert
        viewModel.uiState.first { it.isThrottled }.isThrottled shouldBe true
    }

    @Test
    fun `clear resets all state`() = runTest {
        // Arrange
        `when`(adminRepository.authenticateAdmin("2012")).thenReturn(
            AdminToken(token = "valid-token", expiresAt = System.currentTimeMillis() + 3600000)
        )

        // Act
        viewModel.onPinEntered("2012")
        advanceUntilIdle()
        viewModel.clear()

        // Assert
        viewModel.uiState.first { !it.isOwnerAuthenticated }.isOwnerAuthenticated shouldBe false
        viewModel.uiState.first { it.failedAttempts == 0 }.failedAttempts shouldBe 0
        viewModel.uiState.first { !it.isThrottled }.isThrottled shouldBe false
    }
}