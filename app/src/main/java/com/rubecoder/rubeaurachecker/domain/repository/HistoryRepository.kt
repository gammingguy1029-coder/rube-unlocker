package com.rubecoder.rubeaurachecker.domain.repository

import com.rubecoder.rubeaurachecker.domain.model.ScanHistoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing scan history.
 */
interface HistoryRepository {
    /**
     * Get all scan history items as a Flow.
     */
    suspend fun getHistoryItems(): Flow<List<ScanHistoryItem>>

    /**
     * Delete a history item by ID.
     */
    suspend fun deleteHistoryItem(id: String): Boolean

    /**
     * Clear all history items.
     */
    suspend fun clearHistory(): Boolean

    /**
     * Add a new scan to history.
     */
    suspend fun addScan(score: Int, timestamp: Long = System.currentTimeMillis()): Boolean
}