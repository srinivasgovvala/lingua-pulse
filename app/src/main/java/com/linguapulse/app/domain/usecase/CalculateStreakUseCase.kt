package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.StreakState
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

class CalculateStreakUseCase {

    operator fun invoke(currentState: StreakState, todayDate: LocalDate = LocalDate.now()): StreakCalculationResult {
        val lastDateStr = currentState.lastCompletedDate
        val todayStr = todayDate.toString()

        if (lastDateStr == null) {
            val updated = currentState.copy(
                currentStreak = 1,
                bestStreak = max(1, currentState.bestStreak),
                lastCompletedDate = todayStr
            )
            return StreakCalculationResult(
                updatedState = updated,
                wasStreakIncremented = true,
                wasFreezeUsed = false
            )
        }

        val lastDate = LocalDate.parse(lastDateStr)
        val daysDiff = ChronoUnit.DAYS.between(lastDate, todayDate)

        return when {
            daysDiff <= 0 -> {
                // Already completed a lesson today or clock sync quirk
                StreakCalculationResult(
                    updatedState = currentState,
                    wasStreakIncremented = false,
                    wasFreezeUsed = false
                )
            }
            daysDiff == 1L -> {
                // Exact consecutive day
                val newStreak = currentState.currentStreak + 1
                val updated = currentState.copy(
                    currentStreak = newStreak,
                    bestStreak = max(newStreak, currentState.bestStreak),
                    lastCompletedDate = todayStr
                )
                StreakCalculationResult(
                    updatedState = updated,
                    wasStreakIncremented = true,
                    wasFreezeUsed = false
                )
            }
            daysDiff == 2L -> {
                // Missed single calendar day
                if (currentState.streakFreezesAvailable > 0) {
                    val newStreak = currentState.currentStreak + 1
                    val updated = currentState.copy(
                        currentStreak = newStreak,
                        bestStreak = max(newStreak, currentState.bestStreak),
                        lastCompletedDate = todayStr,
                        streakFreezesAvailable = currentState.streakFreezesAvailable - 1
                    )
                    StreakCalculationResult(
                        updatedState = updated,
                        wasStreakIncremented = true,
                        wasFreezeUsed = true
                    )
                } else {
                    // Streak broken, reset to 1
                    val updated = currentState.copy(
                        currentStreak = 1,
                        lastCompletedDate = todayStr
                    )
                    StreakCalculationResult(
                        updatedState = updated,
                        wasStreakIncremented = false,
                        wasFreezeUsed = false
                    )
                }
            }
            else -> {
                // Multi-day gap (>2 days)
                val updated = currentState.copy(
                    currentStreak = 1,
                    lastCompletedDate = todayStr
                )
                StreakCalculationResult(
                    updatedState = updated,
                    wasStreakIncremented = false,
                    wasFreezeUsed = false
                )
            }
        }
    }
}

data class StreakCalculationResult(
    val updatedState: StreakState,
    val wasStreakIncremented: Boolean,
    val wasFreezeUsed: Boolean
)
