package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.UserProgress
import com.linguapulse.app.domain.repository.LessonRepository
import com.linguapulse.app.domain.repository.OfflineQueueRepository
import com.linguapulse.app.domain.repository.UserProgressRepository
import java.time.LocalDate

class ProcessLessonCompletionUseCase(
    private val lessonRepository: LessonRepository,
    private val userProgressRepository: UserProgressRepository,
    private val offlineQueueRepository: OfflineQueueRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase = CalculateStreakUseCase(),
    private val calculateXpUseCase: CalculateXpUseCase = CalculateXpUseCase()
) {

    data class CompletionSummary(
        val lessonId: String,
        val earnedXp: Int,
        val streakResult: StreakCalculationResult,
        val nextLessonUnlockedId: String?
    )

    suspend operator fun invoke(
        lessonId: String,
        sessionToken: String,
        isPracticeMode: Boolean,
        maxComboStreak: Int,
        mistakesCount: Int,
        todayDate: LocalDate = LocalDate.now()
    ): CompletionSummary {
        // 1. Calculate XP with duplicate prevention
        val xpResult = calculateXpUseCase(
            sessionToken = sessionToken,
            isPracticeMode = isPracticeMode,
            maxComboStreak = maxComboStreak,
            mistakesCount = mistakesCount
        )

        // 2. Fetch current user progress
        val currentProgress = userProgressRepository.getUserProgress()

        // 3. Calculate streak updates
        val streakResult = calculateStreakUseCase(
            currentState = currentProgress.streakState,
            todayDate = todayDate
        )

        // 4. Update lesson status & unlock next
        val nextUnlockedId = lessonRepository.completeLessonAndUnlockNext(lessonId)

        // 5. Update user progress entity
        val updatedProgress = currentProgress.copy(
            totalXp = currentProgress.totalXp + xpResult.totalXpEarned,
            streakState = streakResult.updatedState
        )
        userProgressRepository.saveUserProgress(updatedProgress)

        // 6. Enqueue offline mutation for server sync
        offlineQueueRepository.enqueueMutation(
            lessonId = lessonId,
            earnedXp = xpResult.totalXpEarned,
            timestamp = System.currentTimeMillis()
        )

        return CompletionSummary(
            lessonId = lessonId,
            earnedXp = xpResult.totalXpEarned,
            streakResult = streakResult,
            nextLessonUnlockedId = nextUnlockedId
        )
    }
}
