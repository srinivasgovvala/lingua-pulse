package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.HeartsState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HeartsRegenerationUseCaseTest {

    private lateinit var useCase: HeartsRegenerationUseCase

    @Before
    fun setUp() {
        useCase = HeartsRegenerationUseCase()
    }

    @Test
    fun `deductHeart decreases hearts by 1 and sets timestamp on first deduction`() {
        val fullState = HeartsState(currentHearts = 5, maxHearts = 5, lastDeductionTimestampMs = 0L)
        val now = 1000000L

        val result = useCase.deductHeart(fullState, now)

        assertEquals(4, result.currentHearts)
        assertEquals(now, result.lastDeductionTimestampMs)
    }

    @Test
    fun `deductHeart never reduces hearts below 0`() {
        val emptyState = HeartsState(currentHearts = 0, maxHearts = 5, lastDeductionTimestampMs = 1000000L)
        val result = useCase.deductHeart(emptyState, 2000000L)
        assertEquals(0, result.currentHearts)
    }

    @Test
    fun `refillFromPractice increases hearts by 1 up to maximum 5`() {
        val state = HeartsState(currentHearts = 4, maxHearts = 5, lastDeductionTimestampMs = 1000000L)
        val result = useCase.refillFromPractice(state)
        assertEquals(5, result.currentHearts)
        assertEquals(0L, result.lastDeductionTimestampMs)

        // Cannot exceed 5
        val alreadyFull = useCase.refillFromPractice(result)
        assertEquals(5, alreadyFull.currentHearts)
    }

    @Test
    fun `passive regeneration adds 1 heart per 4 hours elapsed`() {
        val fourHoursMs = 4 * 60 * 60 * 1000L
        val baseTime = 10000000L
        val depletedState = HeartsState(
            currentHearts = 2,
            maxHearts = 5,
            lastDeductionTimestampMs = baseTime,
            refillIntervalMs = fourHoursMs
        )

        // Exactly 4 hours later: +1 Heart -> 3
        val after4Hours = useCase.calculatePassiveRegeneration(depletedState, baseTime + fourHoursMs)
        assertEquals(3, after4Hours.currentHearts)

        // 8 hours later: +2 Hearts -> 4
        val after8Hours = useCase.calculatePassiveRegeneration(depletedState, baseTime + (2 * fourHoursMs))
        assertEquals(4, after8Hours.currentHearts)

        // 24 hours later: clamped to max 5
        val after24Hours = useCase.calculatePassiveRegeneration(depletedState, baseTime + (6 * fourHoursMs))
        assertEquals(5, after24Hours.currentHearts)
    }
}
