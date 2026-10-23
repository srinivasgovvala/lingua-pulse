package com.linguapulse.app.domain.model

data class UserProgress(
    val userId: String = "local_learner",
    val activeCourseId: String = "course_es",
    val streakState: StreakState = StreakState(),
    val heartsState: HeartsState = HeartsState(),
    val totalXp: Int = 0,
    val unlockedUnitIndex: Int = 0,
    val unlockedLessonIndex: Int = 0,
    val gems: Int = 500
)

data class StreakState(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String? = null, // Format: "YYYY-MM-DD"
    val streakFreezesAvailable: Int = 2
)

data class HeartsState(
    val currentHearts: Int = 5,
    val maxHearts: Int = 5,
    val lastDeductionTimestampMs: Long = 0L,
    val refillIntervalMs: Long = 4 * 60 * 60 * 1000L // 4 hours
)
