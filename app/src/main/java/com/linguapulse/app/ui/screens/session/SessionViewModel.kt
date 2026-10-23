package com.linguapulse.app.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.domain.model.Lesson
import com.linguapulse.app.domain.model.ValidationResult
import com.linguapulse.app.domain.repository.LessonRepository
import com.linguapulse.app.domain.repository.UserProgressRepository
import com.linguapulse.app.domain.usecase.HeartsRegenerationUseCase
import com.linguapulse.app.domain.usecase.ProcessLessonCompletionUseCase
import com.linguapulse.app.domain.usecase.ValidateAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.max

data class SessionUiState(
    val isLoading: Boolean = true,
    val lesson: Lesson? = null,
    val activeChallenge: Challenge? = null,
    val totalInitialQuestions: Int = 0,
    val resolvedQuestionsCount: Int = 0,
    val progressFraction: Float = 0f,
    val heartsRemaining: Int = 5,
    val currentCombo: Int = 0,
    val maxCombo: Int = 0,
    val mistakesCount: Int = 0,
    val validationResult: ValidationResult? = null,
    val isSessionComplete: Boolean = false,
    val isOutOfHearts: Boolean = false,
    val earnedXp: Int = 0
)

class SessionViewModel(
    private val lessonId: String,
    private val isPracticeMode: Boolean,
    private val lessonRepository: LessonRepository,
    private val userProgressRepository: UserProgressRepository,
    private val processLessonCompletionUseCase: ProcessLessonCompletionUseCase,
    private val validateAnswerUseCase: ValidateAnswerUseCase = ValidateAnswerUseCase(),
    private val heartsRegenerationUseCase: HeartsRegenerationUseCase = HeartsRegenerationUseCase()
) : ViewModel() {

    private val sessionToken = UUID.randomUUID().toString()
    private val questionQueue = ArrayDeque<Challenge>()
    private var initialTotalCount = 0
    private var solvedCount = 0

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            val userProgress = userProgressRepository.getUserProgress()
            val lesson = if (isPracticeMode) {
                lessonRepository.getReviewLesson()
            } else {
                lessonRepository.getLessonById(lessonId) ?: return@launch
            }

            questionQueue.clear()
            questionQueue.addAll(lesson.challenges)
            initialTotalCount = lesson.challenges.size
            solvedCount = 0

            val firstChallenge = questionQueue.removeFirstOrNull()

            _uiState.value = SessionUiState(
                isLoading = false,
                lesson = lesson,
                activeChallenge = firstChallenge,
                totalInitialQuestions = initialTotalCount,
                resolvedQuestionsCount = 0,
                progressFraction = 0f,
                heartsRemaining = userProgress.heartsState.currentHearts
            )
        }
    }

    fun submitAnswer(submission: Any) {
        val currentChallenge = _uiState.value.activeChallenge ?: return
        if (_uiState.value.validationResult != null) return // Already evaluating

        val result = validateAnswerUseCase(currentChallenge, submission)

        when (result) {
            is ValidationResult.ExactMatch, is ValidationResult.TypoTolerated -> {
                val newCombo = _uiState.value.currentCombo + 1
                val newMaxCombo = max(_uiState.value.maxCombo, newCombo)
                solvedCount++
                val newProgress = solvedCount.toFloat() / initialTotalCount.coerceAtLeast(1)

                _uiState.value = _uiState.value.copy(
                    validationResult = result,
                    currentCombo = newCombo,
                    maxCombo = newMaxCombo,
                    resolvedQuestionsCount = solvedCount,
                    progressFraction = newProgress
                )
            }

            is ValidationResult.Incorrect -> {
                val newMistakes = _uiState.value.mistakesCount + 1
                // Recycle question to end of queue for repetition until mastered
                questionQueue.addLast(currentChallenge)

                var updatedHearts = _uiState.value.heartsRemaining
                if (!isPracticeMode) {
                    val currentHeartsState = com.linguapulse.app.domain.model.HeartsState(currentHearts = updatedHearts)
                    val deducted = heartsRegenerationUseCase.deductHeart(currentHeartsState)
                    updatedHearts = deducted.currentHearts
                    viewModelScope.launch {
                        userProgressRepository.updateHearts(deducted)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    validationResult = result,
                    currentCombo = 0,
                    mistakesCount = newMistakes,
                    heartsRemaining = updatedHearts,
                    isOutOfHearts = updatedHearts <= 0 && !isPracticeMode
                )
            }

            is ValidationResult.EmptySubmission -> {
                _uiState.value = _uiState.value.copy(validationResult = result)
            }
        }
    }

    fun continueNext() {
        if (_uiState.value.validationResult is ValidationResult.EmptySubmission) {
            // Dismiss error banner and let user try again
            _uiState.value = _uiState.value.copy(validationResult = null)
            return
        }

        if (_uiState.value.isOutOfHearts) {
            return // Block until user returns to practice
        }

        if (questionQueue.isEmpty()) {
            completeSession()
        } else {
            val nextQuestion = questionQueue.removeFirstOrNull()
            _uiState.value = _uiState.value.copy(
                activeChallenge = nextQuestion,
                validationResult = null
            )
        }
    }

    private fun completeSession() {
        viewModelScope.launch {
            if (isPracticeMode) {
                // Award +1 Heart
                val progress = userProgressRepository.getUserProgress()
                val refilled = heartsRegenerationUseCase.refillFromPractice(progress.heartsState)
                userProgressRepository.updateHearts(refilled)
            }

            val summary = processLessonCompletionUseCase(
                lessonId = lessonId,
                sessionToken = sessionToken,
                isPracticeMode = isPracticeMode,
                maxComboStreak = _uiState.value.maxCombo,
                mistakesCount = _uiState.value.mistakesCount
            )

            _uiState.value = _uiState.value.copy(
                isSessionComplete = true,
                earnedXp = summary.earnedXp,
                progressFraction = 1f,
                validationResult = null
            )
        }
    }
}
