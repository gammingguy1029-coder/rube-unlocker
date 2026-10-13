package com.rubecoder.rubeaurachecker.domain.util

/**
 * Represents a tier of aura score with a description.
 */
data class AuraScoreTier(
    val minScore: Int,
    val maxScore: Int,
    val description: String
)

object AuraScoreTiers {
    private val tiers = listOf(
        AuraScoreTier(850, 899, "Your aura is steady and grounded."),
        AuraScoreTier(900, 949, "Your aura is bright and expanding."),
        AuraScoreTier(950, 998, "Your aura is vibrant and powerful."),
        AuraScoreTier(999, 999, "PERFECT AURA! You are in perfect harmony.")
    )

    /**
     * Get the tier for a given score.
     * @param score the aura score (between 850 and 999)
     * @return the tier description
     */
    fun getTierForScore(score: Int): AuraScoreTier {
        return tiers.first { it.minScore <= score && score <= it.maxScore }
    }
}