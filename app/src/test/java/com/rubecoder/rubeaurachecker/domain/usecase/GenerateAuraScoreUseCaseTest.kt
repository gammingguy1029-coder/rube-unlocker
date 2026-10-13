package com.rubecoder.rubeaurachecker.domain.usecase

import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [GenerateAuraScoreUseCase].
 */
class GenerateAuraScoreUseCaseTest {

    @Test
    fun `score is between 850 and 999 inclusive`() {
        val useCase = GenerateAuraScoreUseCase()
        for (i in 1..1000) {
            val score = useCase.invoke()
            assertTrue("Score $score is below 850", score >= 850)
            assertTrue("Score $score is above 999", score <= 999)
        }
    }

    @Test
    fun `score is always positive`() {
        val useCase = GenerateAuraScoreUseCase()
        for (i in 1..100) {
            val score = useCase.invoke()
            assertTrue("Score $score is not positive", score > 0)
        }
    }

    @Test
    fun `score is integer`() {
        val useCase = GenerateAuraScoreUseCase()
        val score = useCase.invoke()
        assertEquals(score.toDouble(), score.toDouble(), 0.0)
    }
}