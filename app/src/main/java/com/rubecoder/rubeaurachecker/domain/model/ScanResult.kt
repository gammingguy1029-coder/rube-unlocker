package com.rubecoder.rubeaurachecker.domain.model

/**
 * Represents the result of an aura scan.
 * @param score The aura score (between 850 and 999 for ordinary scans, 9000000 for owner scans).
 * @param timestamp The time the scan was completed.
 */
data class ScanResult(
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()
)