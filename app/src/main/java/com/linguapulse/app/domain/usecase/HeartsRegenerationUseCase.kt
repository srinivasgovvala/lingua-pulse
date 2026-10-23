package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.HeartsState
import kotlin.math.max
import kotlin.math.min

class HeartsRegenerationUseCase {

    fun calculatePassiveRegeneration(
        currentState: HeartsState,
        currentTimeMs: Long = System.currentTimeMillis()
    ): HeartsState {
        if (currentState.currentHearts >= currentState.maxHearts) {
            return currentState
        }

        if (currentState.lastDeductionTimestampMs <= 0L) {
            return currentState
        }

        val elapsedMs = currentTimeMs - currentState.lastDeductionTimestampMs
        if (elapsedMs < currentState.refillIntervalMs) {
            return currentState
        }

        val heartsToAdd = (elapsedMs / currentState.refillIntervalMs).toInt()
        val newHearts = min(currentState.maxHearts, currentState.currentHearts + heartsToAdd)
        val remainingRemainderMs = elapsedMs % currentState.refillIntervalMs
        val newTimestamp = if (newHearts >= currentState.maxHearts) 0L else currentTimeMs - remainingRemainderMs

        return currentState.copy(
            currentHearts = newHearts,
            lastDeductionTimestampMs = newTimestamp
        )
    }

    fun deductHeart(
        currentState: HeartsState,
        currentTimeMs: Long = System.currentTimeMillis()
    ): HeartsState {
        val newCount = max(0, currentState.currentHearts - 1)
        val timestamp = if (currentState.currentHearts == currentState.maxHearts) {
            currentTimeMs
        } else {
            currentState.lastDeductionTimestampMs
        }
        return currentState.copy(
            currentHearts = newCount,
            lastDeductionTimestampMs = timestamp
        )
    }

    fun refillFromPractice(currentState: HeartsState): HeartsState {
        val newCount = min(currentState.maxHearts, currentState.currentHearts + 1)
        val timestamp = if (newCount >= currentState.maxHearts) 0L else currentState.lastDeductionTimestampMs
        return currentState.copy(
            currentHearts = newCount,
            lastDeductionTimestampMs = timestamp
        )
    }
}
