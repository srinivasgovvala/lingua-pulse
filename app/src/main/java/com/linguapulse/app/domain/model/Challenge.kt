package com.linguapulse.app.domain.model

sealed class Challenge {
    abstract val id: String
    abstract val lessonId: String
    abstract val prompt: String

    data class MultipleChoice(
        override val id: String,
        override val lessonId: String,
        override val prompt: String,
        val options: List<String>,
        val correctIndex: Int,
        val audioUrl: String? = null
    ) : Challenge()

    data class TokenArrange(
        override val id: String,
        override val lessonId: String,
        override val prompt: String,
        val targetTokens: List<String>,
        val availableTokens: List<String>
    ) : Challenge()

    data class TextInput(
        override val id: String,
        override val lessonId: String,
        override val prompt: String,
        val acceptedAnswers: List<String>,
        val hint: String? = null
    ) : Challenge()

    data class PairMatching(
        override val id: String,
        override val lessonId: String,
        override val prompt: String,
        val pairs: Map<String, String> // e.g. "Hola" -> "Hello"
    ) : Challenge()

    data class Listening(
        override val id: String,
        override val lessonId: String,
        override val prompt: String,
        val audioAssetPath: String,
        val solutionText: String
    ) : Challenge()
}
