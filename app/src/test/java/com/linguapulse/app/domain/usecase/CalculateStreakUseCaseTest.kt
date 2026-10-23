package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.StreakState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalculateStreakUseCaseTest {

    private lateinit var useCase: CalculateStreakUseCase

    @Before
    fun setUp() {
        useCase = CalculateStreakUseCase()
    }

    @Test
    fun `first lesson completion sets streak to 1`() {
        val state = StreakState(currentStreak = 0, bestStreak = 0, lastCompletedDate = null)
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(1, result.updatedState.currentStreak)
        assertEquals(1, result.updatedState.bestStreak)
        assertEquals("2026-10-22", result.updatedState.lastCompletedDate)
        assertTrue(result.wasStreakIncremented)
        assertFalse(result.wasFreezeUsed)
    }

    @Test
    fun `same day lesson completion preserves existing streak without double counting`() {
        val state = StreakState(currentStreak = 5, bestStreak = 5, lastCompletedDate = "2026-10-22")
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(5, result.updatedState.currentStreak)
        assertFalse(result.wasStreakIncremented)
        assertFalse(result.wasFreezeUsed)
    }

    @Test
    fun `consecutive day lesson completion increments streak by 1`() {
        val state = StreakState(currentStreak = 5, bestStreak = 5, lastCompletedDate = "2026-10-21")
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(6, result.updatedState.currentStreak)
        assertEquals(6, result.updatedState.bestStreak)
        assertEquals("2026-10-22", result.updatedState.lastCompletedDate)
        assertTrue(result.wasStreakIncremented)
        assertFalse(result.wasFreezeUsed)
    }

    @Test
    fun `missed single day with freeze consumes 1 freeze and continues streak`() {
        val state = StreakState(
            currentStreak = 10,
            bestStreak = 10,
            lastCompletedDate = "2026-10-20", // Missed Oct 21
            streakFreezesAvailable = 2
        )
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(11, result.updatedState.currentStreak)
        assertEquals(1, result.updatedState.streakFreezesAvailable) // Consumed 1
        assertEquals("2026-10-22", result.updatedState.lastCompletedDate)
        assertTrue(result.wasStreakIncremented)
        assertTrue(result.wasFreezeUsed)
    }

    @Test
    fun `missed single day without freeze resets streak to 1`() {
        val state = StreakState(
            currentStreak = 10,
            bestStreak = 10,
            lastCompletedDate = "2026-10-20",
            streakFreezesAvailable = 0
        )
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(1, result.updatedState.currentStreak)
        assertEquals(10, result.updatedState.bestStreak) // Best streak retained
        assertEquals("2026-10-22", result.updatedState.lastCompletedDate)
        assertFalse(result.wasStreakIncremented)
        assertFalse(result.wasFreezeUsed)
    }

    @Test
    fun `multi day hiatus resets streak to 1`() {
        val state = StreakState(
            currentStreak = 15,
            bestStreak = 15,
            lastCompletedDate = "2026-10-10",
            streakFreezesAvailable = 2
        )
        val today = LocalDate.of(2026, 10, 22)

        val result = useCase(state, today)

        assertEquals(1, result.updatedState.currentStreak)
        assertEquals(15, result.updatedState.bestStreak)
    }
}
