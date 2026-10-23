package com.linguapulse.app.data.repository

import com.linguapulse.app.data.local.dao.OfflineMutationDao
import com.linguapulse.app.domain.repository.OfflineMutation
import com.linguapulse.app.domain.repository.OfflineQueueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class OfflineQueueRepositoryImpl(
    private val mutationDao: OfflineMutationDao? = null
) : OfflineQueueRepository {

    private val _mutations = MutableStateFlow<List<OfflineMutation>>(emptyList())

    override suspend fun enqueueMutation(lessonId: String, earnedXp: Int, timestamp: Long) {
        val newMutation = OfflineMutation(
            id = UUID.randomUUID().toString(),
            lessonId = lessonId,
            earnedXp = earnedXp,
            timestamp = timestamp,
            isSynced = false
        )
        _mutations.value = _mutations.value + newMutation
    }

    override suspend fun getPendingMutations(): List<OfflineMutation> {
        return _mutations.value.filter { !it.isSynced }
    }

    override suspend fun markAsSynced(mutationId: String) {
        _mutations.value = _mutations.value.map {
            if (it.id == mutationId) it.copy(isSynced = true) else it
        }
    }

    override fun observePendingCount(): Flow<Int> {
        return _mutations.map { list -> list.count { !it.isSynced } }
    }
}
