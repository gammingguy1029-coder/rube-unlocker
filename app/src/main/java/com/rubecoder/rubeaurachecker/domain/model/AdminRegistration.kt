package com.rubecoder.rubeaurachecker.domain.model

/**
 * Represents a registration record for administrative purposes.
 * Includes server-generated fields like id and timestamp.
 */
data class AdminRegistration(
    val id: String,
    val fullName: String,
    val schoolName: String,
    val rollNumber: String,
    val timestamp: Long
)