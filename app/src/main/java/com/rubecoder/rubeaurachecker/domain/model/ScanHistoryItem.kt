package com.rubecoder.rubeaurachecker.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a single scan in the history.
 * @param id Unique identifier for the history item
 * @param score The aura score from the scan
 * @param timestamp When the scan was completed
 */
@Serializable
data class ScanHistoryItem(
    val id: String,
    val score: Int,
    val timestamp: Long
)