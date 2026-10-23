package com.linguapulse.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguapulse.app.domain.model.UserProgress
import com.linguapulse.app.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProgress: UserProgress = UserProgress(),
    val soundEffectsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true
)

class ProfileViewModel(
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userProgressRepository.observeUserProgress().collect { progress ->
                _uiState.value = _uiState.value.copy(userProgress = progress)
            }
        }
    }

    fun toggleSoundEffects() {
        _uiState.value = _uiState.value.copy(soundEffectsEnabled = !_uiState.value.soundEffectsEnabled)
    }

    fun toggleHapticFeedback() {
        _uiState.value = _uiState.value.copy(hapticFeedbackEnabled = !_uiState.value.hapticFeedbackEnabled)
    }
}
