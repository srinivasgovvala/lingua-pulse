package com.linguapulse.app

import android.app.Application
import com.linguapulse.app.data.local.LinguaPulseDatabase
import com.linguapulse.app.data.repository.LessonRepositoryImpl
import com.linguapulse.app.data.repository.OfflineQueueRepositoryImpl
import com.linguapulse.app.data.repository.UserProgressRepositoryImpl
import com.linguapulse.app.domain.repository.LessonRepository
import com.linguapulse.app.domain.repository.OfflineQueueRepository
import com.linguapulse.app.domain.repository.UserProgressRepository
import com.linguapulse.app.domain.usecase.ProcessLessonCompletionUseCase

class LinguaPulseApplication : Application() {

    lateinit var database: LinguaPulseDatabase
        private set

    lateinit var lessonRepository: LessonRepository
        private set

    lateinit var userProgressRepository: UserProgressRepository
        private set

    lateinit var offlineQueueRepository: OfflineQueueRepository
        private set

    lateinit var processLessonCompletionUseCase: ProcessLessonCompletionUseCase
        private set

    override fun onCreate() {
        super.onCreate()

        database = LinguaPulseDatabase.getInstance(this)
        lessonRepository = LessonRepositoryImpl(database.lessonDao())
        userProgressRepository = UserProgressRepositoryImpl(database.userProgressDao())
        offlineQueueRepository = OfflineQueueRepositoryImpl(database.offlineMutationDao())

        processLessonCompletionUseCase = ProcessLessonCompletionUseCase(
            lessonRepository = lessonRepository,
            userProgressRepository = userProgressRepository,
            offlineQueueRepository = offlineQueueRepository
        )
    }
}
