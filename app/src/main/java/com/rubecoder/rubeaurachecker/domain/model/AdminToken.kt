package com.rubecoder.rubeaurachecker.domain.model

/**
 * Represents an admin token obtained from the backend after successful owner authentication.
 * This token is used to authorize admin API calls.
 */
data class AdminToken(
    val token: String,
    val expiresAt: Long // Timestamp in milliseconds
)