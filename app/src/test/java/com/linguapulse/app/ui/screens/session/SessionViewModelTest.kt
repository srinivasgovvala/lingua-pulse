package com.linguapulse.app.ui.screens.session

import com.linguapulse.app.data.repository.LessonRepositoryImpl
import com.linguapulse.app.data.repository.OfflineQueueRepositoryImpl
import com.linguapulse.app.data.repository.UserProgressRepositoryImpl
import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.domain.model.Lesson
import com.linguapulse.app.domain.model.ValidationResult
import com.linguapulse.app.domain.usecase.ProcessLessonCompletionUseCase
import com.linguapulse.app.domain.usecase.ValidateAnswerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var lessonRepo: LessonRepositoryImpl
    private lateinit var progressRepo: UserProgressRepositoryImpl
    private lateinit var queueRepo: OfflineQueueRepositoryImpl
    private lateinit var processCompletionUseCase: ProcessLessonCompletionUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        lessonRepo = LessonRepositoryImpl()
        progressRepo = UserProgressRepositoryImpl()
        queueRepo = OfflineQueueRepositoryImpl()
        processCompletionUseCase = ProcessLessonCompletionUseCase(
            lessonRepository = lessonRepo,
            userProgressRepository = progressRepo,
            offlineQueueRepository = queueRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitting correct answer increases combo and updates progress fraction`() = runTest {
        val viewModel = SessionViewModel(
            lessonId = "lesson_es_1_1",
            isPracticeMode = false,
            lessonRepository = lessonRepo,
            userProgressRepository = progressRepo,
            processLessonCompletionUseCase = processCompletionUseCase
        )
        advanceUntilIdle()

        val initialState = viewModel.uiState.value
        assertFalse(initialState.isLoading)
        assertNotNull(initialState.activeChallenge)
        assertEquals(0, initialState.currentCombo)

        // Challenge 1 is MultipleChoice for "Hello" -> correctIndex = 0 ("Hola")
        viewModel.submitAnswer(0)
        advanceUntilIdle()

        val afterAnswer = viewModel.uiState.value
        assertTrue(afterAnswer.validationResult is ValidationResult.ExactMatch)
        assertEquals(1, afterAnswer.currentCombo)
        assertEquals(1, afterAnswer.maxCombo)
        assertEquals(1, afterAnswer.resolvedQuestionsCount)
    }

    @Test
    fun `submitting incorrect answer deducts 1 heart and resets current combo`() = runTest {
        val viewModel = SessionViewModel(
            lessonId = "lesson_es_1_1",
            isPracticeMode = false,
            lessonRepository = lessonRepo,
            userProgressRepository = progressRepo,
            processLessonCompletionUseCase = processCompletionUseCase
        )
        advanceUntilIdle()

        val initialHearts = viewModel.uiState.value.heartsRemaining

        // Submit wrong option (index 1 is "Adiós")
        viewModel.submitAnswer(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.validationResult is ValidationResult.Incorrect)
        assertEquals(initialHearts - 1, state.heartsRemaining)
        assertEquals(0, state.currentCombo)
        assertEquals(1, state.mistakesCount)
    }
}
