package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.domain.model.ValidationResult
import java.text.Normalizer
import kotlin.math.min

class ValidateAnswerUseCase {

    operator fun invoke(challenge: Challenge, submission: Any): ValidationResult {
        return when (challenge) {
            is Challenge.MultipleChoice -> {
                val selectedIndex = submission as? Int ?: return ValidationResult.EmptySubmission
                if (selectedIndex == challenge.correctIndex) {
                    ValidationResult.ExactMatch
                } else {
                    val correctText = challenge.options.getOrNull(challenge.correctIndex) ?: ""
                    ValidationResult.Incorrect(correctSolution = correctText)
                }
            }

            is Challenge.TokenArrange -> {
                val userTokens = (submission as? List<*>)?.filterIsInstance<String>() ?: return ValidationResult.EmptySubmission
                if (userTokens.isEmpty()) return ValidationResult.EmptySubmission

                val normalizedUser = userTokens.joinToString(" ").trim()
                val normalizedTarget = challenge.targetTokens.joinToString(" ").trim()

                if (normalize(normalizedUser) == normalize(normalizedTarget)) {
                    ValidationResult.ExactMatch
                } else {
                    ValidationResult.Incorrect(correctSolution = challenge.targetTokens.joinToString(" "))
                }
            }

            is Challenge.TextInput -> {
                val text = (submission as? String)?.trim() ?: return ValidationResult.EmptySubmission
                if (text.isBlank()) return ValidationResult.EmptySubmission

                val normalizedInput = normalize(text)
                for (accepted in challenge.acceptedAnswers) {
                    val normalizedAccepted = normalize(accepted)
                    if (normalizedInput == normalizedAccepted) {
                        return ValidationResult.ExactMatch
                    }
                    val distance = levenshteinDistance(normalizedInput, normalizedAccepted)
                    if (distance == 1 && normalizedAccepted.length >= 4) {
                        return ValidationResult.TypoTolerated(suggestedSolution = accepted)
                    }
                }
                ValidationResult.Incorrect(correctSolution = challenge.acceptedAnswers.firstOrNull() ?: "")
            }

            is Challenge.PairMatching -> {
                val matchedPairs = submission as? Map<*, *> ?: return ValidationResult.EmptySubmission
                val allCorrect = challenge.pairs.all { (k, v) -> matchedPairs[k] == v }
                if (allCorrect && matchedPairs.size == challenge.pairs.size) {
                    ValidationResult.ExactMatch
                } else {
                    ValidationResult.Incorrect(correctSolution = "All pairs must match correctly")
                }
            }

            is Challenge.Listening -> {
                val text = (submission as? String)?.trim() ?: return ValidationResult.EmptySubmission
                if (text.isBlank()) return ValidationResult.EmptySubmission

                val normalizedInput = normalize(text)
                val normalizedSolution = normalize(challenge.solutionText)
                if (normalizedInput == normalizedSolution) {
                    ValidationResult.ExactMatch
                } else {
                    val dist = levenshteinDistance(normalizedInput, normalizedSolution)
                    if (dist == 1 && normalizedSolution.length >= 4) {
                        ValidationResult.TypoTolerated(suggestedSolution = challenge.solutionText)
                    } else {
                        ValidationResult.Incorrect(correctSolution = challenge.solutionText)
                    }
                }
            }
        }
    }

    fun normalize(input: String): String {
        val decomposed = Normalizer.normalize(input, Normalizer.Form.NFD)
        val withoutDiacritics = decomposed.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        return withoutDiacritics.lowercase().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ").trim()
    }

    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
