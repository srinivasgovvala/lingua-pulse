package com.linguapulse.app.domain.repository

import kotlinx.coroutines.flow.Flow

data class OfflineMutation(
    val id: String,
    val lessonId: String,
    val earnedXp: Int,
    val timestamp: Long,
    val isSynced: Boolean
)

interface OfflineQueueRepository {
    suspend fun enqueueMutation(lessonId: String, earnedXp: Int, timestamp: Long)
    suspend fun getPendingMutations(): List<OfflineMutation>
    suspend fun markAsSynced(mutationId: String)
    fun observePendingCount(): Flow<Int>
}
