package com.linguapulse.app.domain.model

sealed class ValidationResult {
    data object ExactMatch : ValidationResult()
    data class TypoTolerated(val suggestedSolution: String) : ValidationResult()
    data class Incorrect(val correctSolution: String, val explanation: String? = null) : ValidationResult()
    data object EmptySubmission : ValidationResult()
}
