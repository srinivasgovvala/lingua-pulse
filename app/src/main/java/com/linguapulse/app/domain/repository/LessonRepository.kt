package com.linguapulse.app.domain.repository

import com.linguapulse.app.domain.model.Course
import com.linguapulse.app.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun getActiveCourse(): Flow<Course>
    suspend fun getLessonById(lessonId: String): Lesson?
    suspend fun completeLessonAndUnlockNext(lessonId: String): String?
    suspend fun getReviewLesson(): Lesson
}
