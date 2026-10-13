package com.rubecoder.rubeaurachecker.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material3.icons.filled.FilterList
import androidx.compose.material3.icons.filled.History
import androidx.compose.material3.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.NeonAmber
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.SurfaceDeep
import com.rubecoder.rubeaurachecker.domain.model.ScanHistoryItem
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HistoryScreen(navigateBack: () -> Unit, navigateToResultWithScore: (Int) -> Unit) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val historyItems by viewModel.historyItems.collectAsState()

    // UI state for filters
    val showOnlyPerfectAura by remember { mutableStateOf(false) }
    val selectedSortOption by remember { mutableStateOf("Newest First") }
    val sortOptions = listOf("Newest First", "Oldest First", "Highest Score", "Lowest Score")

    Scaffold(
        topBar = {
            HistoryTopAppBar(
                navigateBack = navigateBack,
                onFilterClicked = { /* TODO: show filter dialog */ },
                onSortClicked = { /* TODO: show sort dialog */ }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PureBlack)
        ) {
            // History header
            HistoryHeader(
                totalItems = historyItems.size,
                showOnlyPerfectAura = showOnlyPerfectAura,
                onShowOnlyPerfectAuraChanged = { showOnlyPerfectAura = it }
            )

            // Filtered and sorted history items
            val filteredItems = historyItems.filter { item ->
                !showOnlyPerfectAura || item.score == 999
            }.sortedWith(
                when (selectedSortOption) {
                    "Newest First" -> compareByDescending { it.timestamp }
                    "Oldest First" -> compareBy { it.timestamp }
                    "Highest Score" -> compareByDescending { it.score }
                    "Lowest Score" -> compareBy { it.score }
                    else -> compareByDescending { it.timestamp }
                }
            )

            // History items list
            if (filteredItems.isEmpty()) {
                HistoryEmptyState()
            } else {
                HistoryItemsList(
                    items = filteredItems,
                    onItemClicked = { viewModel.onHistoryItemClicked(it) },
                    onDeleteClicked = { viewModel.onDeleteHistoryItem(it.id) },
                    navigateToResultWithScore = navigateToResultWithScore
                )
            }

            // Clear history button
            HistoryClearButton(
                onClearClicked = { viewModel.onClearHistory() },
                isEnabled = historyItems.isNotEmpty()
            )

            // Status message
            if (uiState.message.isNotEmpty()) {
                HistoryStatusMessage(
                    message = uiState.message,
                    isError = uiState.isError
                )
            }
        }
    }
}

@Composable
fun HistoryTopAppBar(
    navigateBack: () -> Unit,
    onFilterClicked: () -> Unit,
    onSortClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.history_title),
                color = NeonRed,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = NeonRed
                )
            }
        },
        actions = {
            IconButton(onClick = onFilterClicked) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = NeonRed
                )
            }
            IconButton(onClick = onSortClicked) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = null,
                    tint = NeonRed
                )
            }
        },
        backgroundColor = PureBlack,
        containerColor = PureBlack
    )
}

@Composable
fun HistoryHeader(
    totalItems: Int,
    showOnlyPerfectAura: Boolean,
    onShowOnlyPerfectAuraChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(R.string.history_scan_history),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$totalItems ${stringResource(R.string.history_scans_total)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Toggle for perfect aura only
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.history_perfect_aura_only),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = showOnlyPerfectAura,
                onCheckedChange = { onShowOnlyPerfectAuraChanged(it) },
                colors = SwitchDefaults.switchColors(
                    thumbColor = { if (it) NeonRed else MaterialTheme.colorScheme.onSurfaceVariant },
                    trackColor = { if (it) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) }
                )
            )
        }
    }
}

@Composable
fun HistoryEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.history_no_scans),
            color = NeonRed,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(R.string.history_no_scans_subtext),
            color = MaterialTheme.typography.bodyLarge.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun HistoryItemsList(
    items: List<ScanHistoryItem>,
    onItemClicked: (ScanHistoryItem) -> Unit,
    onDeleteClicked: (String) -> Unit,
    navigateToResultWithScore: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(items) { item ->
            HistoryItem(
                item = item,
                onItemClicked = { onItemClicked(item) },
                onDeleteClicked = { onDeleteClicked(item.id) },
                navigateToResultWithScore = navigateToResultWithScore
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant, alpha = 0.2f)
        }
    }
}

@Composable
fun HistoryItem(
    item: ScanHistoryItem,
    onItemClicked: (ScanHistoryItem) -> Unit,
    onDeleteClicked: (String) -> Unit,
    navigateToResultWithScore: (Int) -> Unit
) {
    val tier = tierForScore(item.score)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .clickable { navigateToResultWithScore(item.score) }
    ) {
        // Icon and info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(
                text = "%,d".format(item.score),
                color = tier.color,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${stringResource(R.string.history_date_label)} ${formatTimestamp(item.timestamp)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Tier badge
            Text(
                text = tier.description,
                color = tier.color,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .background(tier.color.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Delete button
        IconButton(
            onClick = { onDeleteClicked(item.id) },
            color = MaterialTheme.colorScheme.error
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError
            )
        }
    }
}

@Composable
fun HistoryClearButton(
    onClearClicked: () -> Unit,
    isEnabled: Boolean
) {
    Button(
        onClick = onClearClicked,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp)
    ) {
        Text(
            text = stringResource(R.string.history_clear_all),
            color = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HistoryStatusMessage(
    message: String,
    isError: Boolean
) {
    Text(
        text = message,
        color = if (isError) MaterialTheme.colorScheme.error else NeonWhite,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                if (isError) MaterialTheme.colorScheme.errorContainer else SurfaceDeep
            )
            .padding(12.dp)
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val sdf = java.text.SimpleDateFormat("MMM d, yyyy h:mm a", java.util.Locale.getDefault())
    return sdf.format(date)
}

// Aura tier description + accent color for history display. Uses fixed palette
// colors rather than theme lookups so it never needs composition state.
private data class HistoryTier(
    val description: String,
    val color: androidx.compose.ui.graphics.Color
)

private fun tierForScore(score: Int): HistoryTier = when {
    score == 9000000 -> HistoryTier("Owner Aura", NeonRed)
    score == 999 -> HistoryTier("Perfect Aura", NeonAmber)
    score in 950..998 -> HistoryTier("Radiant Aura", NeonRed)
    score in 900..949 -> HistoryTier("Bright Aura", NeonWhite)
    else -> HistoryTier("Steady Aura", NeonWhite)
}