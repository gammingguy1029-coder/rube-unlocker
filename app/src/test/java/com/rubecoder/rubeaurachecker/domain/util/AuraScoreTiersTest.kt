package com.rubecoder.rubeaurachecker.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AuraScoreTiers].
 */
class AuraScoreTiersTest {

    @Test
    fun `test tier for score 850`() {
        val tier = AuraScoreTiers.getTierForScore(850)
        assertEquals(850, tier.minScore)
        assertEquals(899, tier.maxScore)
        assertEquals("Your aura is steady and grounded.", tier.description)
    }

    @Test
    fun `test tier for score 899`() {
        val tier = AuraScoreTiers.getTierForScore(899)
        assertEquals(850, tier.minScore)
        assertEquals(899, tier.maxScore)
        assertEquals("Your aura is steady and grounded.", tier.description)
    }

    @Test
    fun `test tier for score 900`() {
        val tier = AuraScoreTiers.getTierForScore(900)
        assertEquals(900, tier.minScore)
        assertEquals(949, tier.maxScore)
        assertEquals("Your aura is bright and expanding.", tier.description)
    }

    @Test
    fun `test tier for score 949`() {
        val tier = AuraScoreTiers.getTierForScore(949)
        assertEquals(900, tier.minScore)
        assertEquals(949, tier.maxScore)
        assertEquals("Your aura is bright and expanding.", tier.description)
    }

    @Test
    fun `test tier for score 950`() {
        val tier = AuraScoreTiers.getTierForScore(950)
        assertEquals(950, tier.minScore)
        assertEquals(998, tier.maxScore)
        assertEquals("Your aura is vibrant and powerful.", tier.description)
    }

    @Test
    fun `test tier for score 998`() {
        val tier = AuraScoreTiers.getTierForScore(998)
        assertEquals(950, tier.minScore)
        assertEquals(998, tier.maxScore)
        assertEquals("Your aura is vibrant and powerful.", tier.description)
    }

    @Test
    fun `test tier for score 999`() {
        val tier = AuraScoreTiers.getTierForScore(999)
        assertEquals(999, tier.minScore)
        assertEquals(999, tier.maxScore)
        assertEquals("PERFECT AURA! You are in perfect harmony.", tier.description)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test tier for score below 850 throws`() {
        AuraScoreTiers.getTierForScore(849)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test tier for score above 999 throws`() {
        AuraScoreTiers.getTierForScore(1000)
    }
}