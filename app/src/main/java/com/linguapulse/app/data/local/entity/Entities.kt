package com.linguapulse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val unitId: String,
    val courseId: String,
    val title: String,
    val orderIndex: Int,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val challengesJson: String // Serialized JSON of challenges for offline execution
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val activeCourseId: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val lastCompletedDate: String?,
    val streakFreezesAvailable: Int,
    val currentHearts: Int,
    val maxHearts: Int,
    val lastDeductionTimestampMs: Long,
    val totalXp: Int,
    val gems: Int
)

@Entity(tableName = "offline_mutations")
data class OfflineMutationEntity(
    @PrimaryKey val mutationId: String,
    val lessonId: String,
    val earnedXp: Int,
    val timestamp: Long,
    val isSynced: Boolean
)
