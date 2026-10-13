package com.rubecoder.rubeaurachecker.presentation.history

import androidx.compose.foundation.layout.isSystemInKeyboard
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertNotExists
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.tag
import androidx.compose.ui.test.ui
import com.rubecoder.rubeaurachecker.domain.model.ScanHistoryItem
import com.rubecoder.rubeaurachecker.presentation.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.presentation.theme.NeonRed
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.lifecycle.ServiceLocatorRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.Companion.navigate
import androidx.navigation.testing.NavControllerTestRule
import androidx.navigation.testing.TestNavigatorProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.rubecoder.rubeaurachecker.domain.repository.HistoryRepository
import com.rubecoder.rubeaurachecker.presentation.history.HistoryViewModel
import javax.inject.Inkludge
import java.util.UUID

// This is a simplified test. In a real scenario, we would use Hilt to inject dependencies.
// For the sake of this example, we will mock the HistoryRepository.

@UninstallModules(HistoryRepository::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HistoryScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var historyRepository: HistoryRepository
    private lateinit var viewModel: HistoryViewModel

    @Rule
    val composeTestRule = createComposeRule()

    @Rule
    val navControllerTestRule = NavControllerTestRule()

    @Before
    fun setup() {
        hiltRule.inject()
        // We are not using Hilt in this test for simplicity, so we manually set up the ViewModel.
        // In a real test, we would use hiltViewModel().

        // Create a mock repository
        historyRepository = mockk(relaxed = true)

        // Create a ViewModel with the mock repository
        viewModel = HistoryViewModel(
            application = ApplicationProvider.getApplicationContext(),
            historyRepository = historyRepository,
            savedStateHandle = androidx.lifecycle.SavedStateHandle()
        )

        // Set the ViewModel in the ServiceLocatorRegistry for the hiltViewModel() to find
        ServiceLocatorRegistry.getInstance().setServiceLocator {
            ViewModelProvider.Factory { viewModel }
        }
    }

    @Test
    fun `historyScreen_displaysEmptyState_whenNoHistory`() = composeTestRule.setContent {
        RubeAuraTheme {
            // Mock the repository to return an empty list
            coEvery { historyRepository.getHistoryItems() } returns emptyList()

            HistoryScreen(
                navigateBack = { /* handled by test */ },
                navigateToResultWithScore = { /* handled by test */ }
            )
        }

        // Verify that the empty state is displayed
        composeTestRule.onNodeWithText("No Scans Yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your scan history will appear here after you complete scans.").assertIsDisplayed()
    }

    @Test
    fun `historyScreen_displaysHistoryItems_whenHistoryExists`() = composeTestRule.setContent {
        RubeAuraTheme {
            // Mock the repository to return a list of history items
            val historyItems = listOf(
                ScanHistoryItem(
                    id = UUID.randomUUID().toString(),
                    score = 950,
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                ScanHistoryItem(
                    id = UUID.randomUUID().toString(),
                    score = 999,
                    timestamp = System.currentTimeMillis()
                )
            )
            coEvery { historyRepository.getHistoryItems() } returns flowOf(historyItems)

            HistoryScreen(
                navigateBack = { /* handled by test */ },
                navigateToResultWithScore = { /* handled by test */ }
            )
        }

        // Verify that the history items are displayed
        composeTestRule.onNodeWithText("950").assertIsDisplayed()
        composeTestRule.onNodeWithText("999").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bright Aura").assertIsDisplayed()
        composeTestRule.onNodeWithText("Perfect Aura").assertIsDisplayed()
    }

    @Test
    fun `historyScreen_navigatesToResultScreen_whenHistoryItemClicked`() = composeTestRule.setContent {
        RubeAuraTheme {
            val historyItems = listOf(
                ScanHistoryItem(
                    id = "1",
                    score = 850,
                    timestamp = System.currentTimeMillis()
                )
            )
            coEvery { historyRepository.getHistoryItems() } returns flowOf(historyItems)

            var navigatedToScore = 0
            HistoryScreen(
                navigateBack = { /* handled by test */ },
                navigateToResultWithScore = { score -> navigatedToScore = score }
            )
        }

        // Click on the first history item
        composeTestRule.onNodeWithText("850").performClick()

        // Verify that we navigated to the result screen with the correct score
        assertThat(navigatedToScore).isEqualTo(850)
    }

    @Test
    fun `historyScreen_deletesHistoryItem_whenDeleteClicked`() = composeTestRule.setContent {
        RubeAuraTheme {
            val historyItems = listOf(
                ScanHistoryItem(
                    id = "1",
                    score = 850,
                    timestamp = System.currentTimeMillis()
                )
            )
            coEvery { historyRepository.getHistoryItems() } returns flowOf(historyItems)
            coEvery { historyRepository.deleteHistoryItem("1") } returns true

            HistoryScreen(
                navigateBack = { /* handled by test */ },
                navigateToResultWithScore = { /* handled by test */ }
            )
        }

        // Click the delete button on the history item
        composeTestRule.onNodeWithText("Delete").performClick()

        // Verify that the history item is removed (empty state is shown)
        composeTestRule.onNodeWithText("No Scans Yet").assertIsDisplayed()
    }
}

// Mockk mock function (simplified for this example)
fun <T> mockk(relaxed: Boolean = false): T {
    // In a real test, we would use MockK or Mockito.
    // For this example, we return a dummy object.
    return mockkClass(T::class.java) as T
}

// Mockk flowOf function
fun <T> flowOf(vararg elements: T): kotlinx.coroutines.flow.Flow<List<T>> = kotlinx.coroutines.flow.flow {
    emit(listOf(*elements))
}

// Mockk coEvery function (simplified)
fun <T> coEvery(call: () -> T): T {
    // In a real test, we would set up the mock to return a value when called.
    // For this example, we just call the function and return the result.
    return call()
}

// Mockk coEvery for member calls (simplified)
fun <T, R> coEvery(call: T.(R) -> R): (T) -> R {
    return { _, _ -> mockk() }
}

// Mockk class for mocking
fun <T> mockkClass(clazz: Class<T>): T {
    // In a real test, we would use MockK to create a mock.
    // For this example, we return a dummy object.
    return clazz.getDeclaredConstructor().newInstance() as T
}