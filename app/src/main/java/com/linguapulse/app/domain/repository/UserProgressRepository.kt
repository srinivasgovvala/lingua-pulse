package com.linguapulse.app.domain.repository

import com.linguapulse.app.domain.model.HeartsState
import com.linguapulse.app.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    fun observeUserProgress(): Flow<UserProgress>
    suspend fun getUserProgress(): UserProgress
    suspend fun saveUserProgress(progress: UserProgress)
    suspend fun updateHearts(heartsState: HeartsState)
}
