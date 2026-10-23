package com.linguapulse.app.domain.usecase

import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateAnswerUseCaseTest {

    private lateinit var useCase: ValidateAnswerUseCase

    @Before
    fun setUp() {
        useCase = ValidateAnswerUseCase()
    }

    @Test
    fun `multiple choice with correct index returns ExactMatch`() {
        val challenge = Challenge.MultipleChoice(
            id = "c1",
            lessonId = "l1",
            prompt = "Select 'Hello'",
            options = listOf("Hola", "Adiós", "Gracias"),
            correctIndex = 0
        )
        val result = useCase(challenge, 0)
        assertTrue(result is ValidationResult.ExactMatch)
    }

    @Test
    fun `multiple choice with wrong index returns Incorrect`() {
        val challenge = Challenge.MultipleChoice(
            id = "c1",
            lessonId = "l1",
            prompt = "Select 'Hello'",
            options = listOf("Hola", "Adiós", "Gracias"),
            correctIndex = 0
        )
        val result = useCase(challenge, 1)
        assertTrue(result is ValidationResult.Incorrect)
        assertEquals("Hola", (result as ValidationResult.Incorrect).correctSolution)
    }

    @Test
    fun `token arrange with exact token sequence returns ExactMatch`() {
        val challenge = Challenge.TokenArrange(
            id = "c2",
            lessonId = "l1",
            prompt = "Translate 'Good morning'",
            targetTokens = listOf("Buenos", "días"),
            availableTokens = listOf("Buenos", "días", "noches")
        )
        val result = useCase(challenge, listOf("Buenos", "días"))
        assertTrue(result is ValidationResult.ExactMatch)
    }

    @Test
    fun `text input with accent variations returns ExactMatch due to normalization`() {
        val challenge = Challenge.TextInput(
            id = "c3",
            lessonId = "l1",
            prompt = "Translate 'The coffee'",
            acceptedAnswers = listOf("El café")
        )
        // User omits accent mark 'e' instead of 'é'
        val result = useCase(challenge, "El cafe")
        assertTrue(result is ValidationResult.ExactMatch)
    }

    @Test
    fun `text input with single typo on long word returns TypoTolerated`() {
        val challenge = Challenge.TextInput(
            id = "c4",
            lessonId = "l1",
            prompt = "Translate 'Thank you'",
            acceptedAnswers = listOf("Gracias")
        )
        // User types 'Graciss' (Levenshtein distance 1)
        val result = useCase(challenge, "Graciss")
        assertTrue(result is ValidationResult.TypoTolerated)
        assertEquals("Gracias", (result as ValidationResult.TypoTolerated).suggestedSolution)
    }

    @Test
    fun `text input with empty or blank submission returns EmptySubmission`() {
        val challenge = Challenge.TextInput(
            id = "c5",
            lessonId = "l1",
            prompt = "Translate 'Please'",
            acceptedAnswers = listOf("Por favor")
        )
        val emptyResult = useCase(challenge, "")
        val whitespaceResult = useCase(challenge, "   ")
        assertTrue(emptyResult is ValidationResult.EmptySubmission)
        assertTrue(whitespaceResult is ValidationResult.EmptySubmission)
    }

    @Test
    fun `pair matching with all correct associations returns ExactMatch`() {
        val challenge = Challenge.PairMatching(
            id = "c6",
            lessonId = "l1",
            prompt = "Match pairs",
            pairs = mapOf("Hola" to "Hello", "Adiós" to "Goodbye")
        )
        val result = useCase(challenge, mapOf("Hola" to "Hello", "Adiós" to "Goodbye"))
        assertTrue(result is ValidationResult.ExactMatch)
    }

    @Test
    fun `pair matching with incomplete or mismatched map returns Incorrect`() {
        val challenge = Challenge.PairMatching(
            id = "c6",
            lessonId = "l1",
            prompt = "Match pairs",
            pairs = mapOf("Hola" to "Hello", "Adiós" to "Goodbye")
        )
        val mismatch = useCase(challenge, mapOf("Hola" to "Goodbye", "Adiós" to "Hello"))
        assertTrue(mismatch is ValidationResult.Incorrect)
    }
}
