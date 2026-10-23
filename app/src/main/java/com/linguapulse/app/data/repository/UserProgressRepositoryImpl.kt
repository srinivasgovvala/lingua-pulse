package com.linguapulse.app.data.repository

import com.linguapulse.app.data.local.dao.UserProgressDao
import com.linguapulse.app.domain.model.HeartsState
import com.linguapulse.app.domain.model.UserProgress
import com.linguapulse.app.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserProgressRepositoryImpl(
    private val userProgressDao: UserProgressDao? = null
) : UserProgressRepository {

    private val _progressFlow = MutableStateFlow(UserProgress())

    override fun observeUserProgress(): Flow<UserProgress> = _progressFlow.asStateFlow()

    override suspend fun getUserProgress(): UserProgress = _progressFlow.value

    override suspend fun saveUserProgress(progress: UserProgress) {
        _progressFlow.value = progress
    }

    override suspend fun updateHearts(heartsState: HeartsState) {
        _progressFlow.value = _progressFlow.value.copy(heartsState = heartsState)
    }
}
