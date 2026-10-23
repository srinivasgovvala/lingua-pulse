package com.linguapulse.app.domain.model

data class Course(
    val id: String,
    val title: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val flagEmoji: String,
    val units: List<UnitModel>
)

data class UnitModel(
    val id: String,
    val courseId: String,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val unitId: String,
    val title: String,
    val orderIndex: Int,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val challenges: List<Challenge> = emptyList()
)
