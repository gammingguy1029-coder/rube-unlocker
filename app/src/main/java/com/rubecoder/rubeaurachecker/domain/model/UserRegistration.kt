package com.rubecoder.rubeaurachecker.domain.model

/**
 * Represents a user registration.
 * @param fullName The user's full name.
 * @param schoolName The user's school name.
 * @param rollNumber The user's roll number (as a string to preserve leading zeros).
 */
data class UserRegistration(
    val fullName: String,
    val schoolName: String,
    val rollNumber: String
)