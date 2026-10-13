package com.rubecoder.rubeaurachecker.domain.usecase

import kotlin.random.Random

/**
 * Generates a random aura score between 850 and 999 (inclusive).
 */
class GenerateAuraScoreUseCase {
    operator fun invoke(): Int {
        return 850 + Random.nextInt(150) // 0..149 -> 850..999
    }
}