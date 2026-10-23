package com.linguapulse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linguapulse.app.data.local.entity.LessonEntity
import com.linguapulse.app.data.local.entity.OfflineMutationEntity
import com.linguapulse.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: String)

    @Query("UPDATE lessons SET isUnlocked = 1 WHERE id = :lessonId")
    suspend fun unlockLesson(lessonId: String)
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId LIMIT 1")
    fun observeProgress(userId: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE userId = :userId LIMIT 1")
    suspend fun getProgress(userId: String): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET currentHearts = :hearts, lastDeductionTimestampMs = :timestamp WHERE userId = :userId")
    suspend fun updateHearts(userId: String, hearts: Int, timestamp: Long)
}

@Dao
interface OfflineMutationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(mutation: OfflineMutationEntity)

    @Query("SELECT * FROM offline_mutations WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getPending(): List<OfflineMutationEntity>

    @Query("UPDATE offline_mutations SET isSynced = 1 WHERE mutationId = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT COUNT(*) FROM offline_mutations WHERE isSynced = 0")
    fun observePendingCount(): Flow<Int>
}
