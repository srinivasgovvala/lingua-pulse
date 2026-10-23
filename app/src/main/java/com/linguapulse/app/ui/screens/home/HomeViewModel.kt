package com.linguapulse.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguapulse.app.domain.model.Course
import com.linguapulse.app.domain.model.HeartsState
import com.linguapulse.app.domain.model.StreakState
import com.linguapulse.app.domain.model.UserProgress
import com.linguapulse.app.domain.repository.LessonRepository
import com.linguapulse.app.domain.repository.OfflineQueueRepository
import com.linguapulse.app.domain.repository.UserProgressRepository
import com.linguapulse.app.domain.usecase.HeartsRegenerationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val userProgress: UserProgress = UserProgress(),
    val pendingSyncCount: Int = 0,
    val showHeartsEmptyDialog: Boolean = false
)

class HomeViewModel(
    private val lessonRepository: LessonRepository,
    private val userProgressRepository: UserProgressRepository,
    private val offlineQueueRepository: OfflineQueueRepository,
    private val heartsRegenerationUseCase: HeartsRegenerationUseCase = HeartsRegenerationUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Apply passive hearts regeneration on resume/start
            val currentProgress = userProgressRepository.getUserProgress()
            val regeneratedHearts = heartsRegenerationUseCase.calculatePassiveRegeneration(currentProgress.heartsState)
            if (regeneratedHearts.currentHearts != currentProgress.heartsState.currentHearts) {
                userProgressRepository.updateHearts(regeneratedHearts)
            }

            combine(
                lessonRepository.getActiveCourse(),
                userProgressRepository.observeUserProgress(),
                offlineQueueRepository.observePendingCount()
            ) { course, progress, pendingCount ->
                HomeUiState(
                    isLoading = false,
                    course = course,
                    userProgress = progress,
                    pendingSyncCount = pendingCount,
                    showHeartsEmptyDialog = progress.heartsState.currentHearts == 0
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
