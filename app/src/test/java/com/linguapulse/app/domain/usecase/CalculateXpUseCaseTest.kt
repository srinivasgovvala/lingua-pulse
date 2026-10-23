package com.linguapulse.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateXpUseCaseTest {

    private lateinit var useCase: CalculateXpUseCase

    @Before
    fun setUp() {
        useCase = CalculateXpUseCase()
    }

    @Test
    fun `standard lesson with zero mistakes calculates base xp, combo bonus, and perfect bonus`() {
        val result = useCase(
            sessionToken = "session_token_1",
            isPracticeMode = false,
            maxComboStreak = 6, // 6 / 2 = 3 combo bonus
            mistakesCount = 0   // perfect bonus = 5
        )

        assertEquals(10, result.baseXp)
        assertEquals(3, result.comboBonusXp)
        assertEquals(5, result.perfectBonusXp)
        assertEquals(18, result.totalXpEarned) // 10 + 3 + 5
        assertFalse(result.isDuplicateSubmission)
    }

    @Test
    fun `standard lesson with mistakes omits perfect bonus`() {
        val result = useCase(
            sessionToken = "session_token_2",
            isPracticeMode = false,
            maxComboStreak = 4, // 4 / 2 = 2 combo bonus
            mistakesCount = 2   // no perfect bonus
        )

        assertEquals(10, result.baseXp)
        assertEquals(2, result.comboBonusXp)
        assertEquals(0, result.perfectBonusXp)
        assertEquals(12, result.totalXpEarned)
        assertFalse(result.isDuplicateSubmission)
    }

    @Test
    fun `practice mode returns flat 5 xp without combo or perfect bonuses`() {
        val result = useCase(
            sessionToken = "session_token_practice",
            isPracticeMode = true,
            maxComboStreak = 8,
            mistakesCount = 0
        )

        assertEquals(5, result.totalXpEarned)
        assertEquals(5, result.baseXp)
        assertEquals(0, result.comboBonusXp)
        assertEquals(0, result.perfectBonusXp)
    }

    @Test
    fun `duplicate submission with same session token returns 0 xp and flags duplicate`() {
        val token = "idempotent_session_token_999"

        val firstRun = useCase(
            sessionToken = token,
            isPracticeMode = false,
            maxComboStreak = 5,
            mistakesCount = 0
        )
        assertEquals(17, firstRun.totalXpEarned)
        assertFalse(firstRun.isDuplicateSubmission)

        // Attempt replay with exact same token
        val secondRun = useCase(
            sessionToken = token,
            isPracticeMode = false,
            maxComboStreak = 5,
            mistakesCount = 0
        )
        assertEquals(0, secondRun.totalXpEarned)
        assertTrue(secondRun.isDuplicateSubmission)
    }
}
