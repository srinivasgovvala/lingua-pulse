package com.linguapulse.app.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguapulse.app.domain.model.HeartsState
import com.linguapulse.app.domain.repository.UserProgressRepository
import com.linguapulse.app.domain.usecase.HeartsRegenerationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val heartsRemaining: Int = 5,
    val maxHearts: Int = 5,
    val weakWordsCount: Int = 12
)

class ReviewViewModel(
    private val userProgressRepository: UserProgressRepository,
    private val heartsRegenerationUseCase: HeartsRegenerationUseCase = HeartsRegenerationUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userProgressRepository.observeUserProgress().collect { progress ->
                _uiState.value = ReviewUiState(
                    heartsRemaining = progress.heartsState.currentHearts,
                    maxHearts = progress.heartsState.maxHearts
                )
            }
        }
    }
}
