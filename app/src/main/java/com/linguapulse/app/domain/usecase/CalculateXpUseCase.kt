package com.linguapulse.app.domain.usecase

import kotlin.math.min

class CalculateXpUseCase(
    private val processedSessionTokens: MutableSet<String> = mutableSetOf()
) {

    data class XpCalculationResult(
        val totalXpEarned: Int,
        val baseXp: Int,
        val comboBonusXp: Int,
        val perfectBonusXp: Int,
        val isDuplicateSubmission: Boolean
    )

    operator fun invoke(
        sessionToken: String,
        isPracticeMode: Boolean,
        maxComboStreak: Int,
        mistakesCount: Int
    ): XpCalculationResult {
        // Idempotency: prevent replay attack or duplicate reward
        if (processedSessionTokens.contains(sessionToken)) {
            return XpCalculationResult(
                totalXpEarned = 0,
                baseXp = 0,
                comboBonusXp = 0,
                perfectBonusXp = 0,
                isDuplicateSubmission = true
            )
        }

        processedSessionTokens.add(sessionToken)

        if (isPracticeMode) {
            return XpCalculationResult(
                totalXpEarned = 5,
                baseXp = 5,
                comboBonusXp = 0,
                perfectBonusXp = 0,
                isDuplicateSubmission = false
            )
        }

        val baseXp = 10
        val comboBonus = min(5, maxComboStreak / 2)
        val perfectBonus = if (mistakesCount == 0) 5 else 0
        val total = baseXp + comboBonus + perfectBonus

        return XpCalculationResult(
            totalXpEarned = total,
            baseXp = baseXp,
            comboBonusXp = comboBonus,
            perfectBonusXp = perfectBonus,
            isDuplicateSubmission = false
        )
    }

    fun isTokenProcessed(token: String): Boolean = processedSessionTokens.contains(token)
}
