package com.linguapulse.app.data.repository

import com.linguapulse.app.data.local.dao.LessonDao
import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.domain.model.Course
import com.linguapulse.app.domain.model.Lesson
import com.linguapulse.app.domain.model.UnitModel
import com.linguapulse.app.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LessonRepositoryImpl(
    private val lessonDao: LessonDao? = null
) : LessonRepository {

    // In-memory backing state for fast responsive UI & offline fallback
    private val _courseFlow = MutableStateFlow(createSeedCourse())

    override fun getActiveCourse(): Flow<Course> = _courseFlow.asStateFlow()

    override suspend fun getLessonById(lessonId: String): Lesson? {
        val course = _courseFlow.value
        for (unit in course.units) {
            val found = unit.lessons.find { it.id == lessonId }
            if (found != null) return found
        }
        return null
    }

    override suspend fun completeLessonAndUnlockNext(lessonId: String): String? {
        val course = _courseFlow.value
        var nextLessonToUnlock: String? = null
        var foundCurrent = false

        val updatedUnits = course.units.map { unit ->
            val updatedLessons = unit.lessons.map { lesson ->
                if (lesson.id == lessonId) {
                    foundCurrent = true
                    lesson.copy(isCompleted = true)
                } else if (foundCurrent && nextLessonToUnlock == null && !lesson.isUnlocked) {
                    nextLessonToUnlock = lesson.id
                    lesson.copy(isUnlocked = true)
                } else {
                    lesson
                }
            }
            unit.copy(lessons = updatedLessons)
        }

        _courseFlow.value = course.copy(units = updatedUnits)
        return nextLessonToUnlock
    }

    override suspend fun getReviewLesson(): Lesson {
        return Lesson(
            id = "practice_review_session",
            unitId = "unit_practice",
            title = "Heart Recovery Practice",
            orderIndex = 999,
            isUnlocked = true,
            isCompleted = false,
            challenges = listOf(
                Challenge.MultipleChoice(
                    id = "p_rev_1",
                    lessonId = "practice_review_session",
                    prompt = "How do you say 'Thank you very much' in Spanish?",
                    options = listOf("Muchas gracias", "Por favor", "De nada", "Buenas noches"),
                    correctIndex = 0
                ),
                Challenge.TextInput(
                    id = "p_rev_2",
                    lessonId = "practice_review_session",
                    prompt = "Translate 'The coffee' into Spanish",
                    acceptedAnswers = listOf("El café", "El cafe", "un cafe")
                ),
                Challenge.TokenArrange(
                    id = "p_rev_3",
                    lessonId = "practice_review_session",
                    prompt = "Assemble: 'Good morning, friend'",
                    targetTokens = listOf("Buenos", "días", "amigo"),
                    availableTokens = listOf("días", "hola", "Buenos", "tardes", "amigo")
                ),
                Challenge.PairMatching(
                    id = "p_rev_4",
                    lessonId = "practice_review_session",
                    prompt = "Match the vocabulary pairs",
                    pairs = mapOf(
                        "Agua" to "Water",
                        "Pan" to "Bread",
                        "Leche" to "Milk",
                        "Manzana" to "Apple"
                    )
                )
            )
        )
    }

    companion object {
        fun createSeedCourse(): Course {
            val unit1Lessons = listOf(
                Lesson(
                    id = "lesson_es_1_1",
                    unitId = "unit_es_1",
                    title = "Greetings & Basics",
                    orderIndex = 1,
                    isUnlocked = true,
                    isCompleted = false,
                    challenges = listOf(
                        Challenge.MultipleChoice(
                            id = "c_es_101",
                            lessonId = "lesson_es_1_1",
                            prompt = "Which of these means 'Hello'?",
                            options = listOf("Hola", "Adiós", "Gracias", "Por favor"),
                            correctIndex = 0
                        ),
                        Challenge.TokenArrange(
                            id = "c_es_102",
                            lessonId = "lesson_es_1_1",
                            prompt = "Translate 'Good morning'",
                            targetTokens = listOf("Buenos", "días"),
                            availableTokens = listOf("noches", "Buenos", "días", "tardes")
                        ),
                        Challenge.TextInput(
                            id = "c_es_103",
                            lessonId = "lesson_es_1_1",
                            prompt = "Type 'Please' in Spanish",
                            acceptedAnswers = listOf("Por favor", "porfavor")
                        ),
                        Challenge.PairMatching(
                            id = "c_es_104",
                            lessonId = "lesson_es_1_1",
                            prompt = "Match the greetings",
                            pairs = mapOf(
                                "Hola" to "Hello",
                                "Adiós" to "Goodbye",
                                "Gracias" to "Thank you",
                                "Sí" to "Yes"
                            )
                        ),
                        Challenge.MultipleChoice(
                            id = "c_es_105",
                            lessonId = "lesson_es_1_1",
                            prompt = "What does 'De nada' mean?",
                            options = listOf("You are welcome", "Excuse me", "Good night", "See you later"),
                            correctIndex = 0
                        )
                    )
                ),
                Lesson(
                    id = "lesson_es_1_2",
                    unitId = "unit_es_1",
                    title = "Introductions",
                    orderIndex = 2,
                    isUnlocked = false,
                    isCompleted = false,
                    challenges = listOf(
                        Challenge.TextInput(
                            id = "c_es_106",
                            lessonId = "lesson_es_1_2",
                            prompt = "Translate 'I am' into Spanish",
                            acceptedAnswers = listOf("Yo soy", "Soy")
                        ),
                        Challenge.TokenArrange(
                            id = "c_es_107",
                            lessonId = "lesson_es_1_2",
                            prompt = "Assemble: 'My name is Carlos'",
                            targetTokens = listOf("Mi", "nombre", "es", "Carlos"),
                            availableTokens = listOf("es", "Mi", "Carlos", "tu", "nombre", "soy")
                        )
                    )
                ),
                Lesson(
                    id = "lesson_es_1_3",
                    unitId = "unit_es_1",
                    title = "Unit 1 Milestone Trophy",
                    orderIndex = 3,
                    isUnlocked = false,
                    isCompleted = false,
                    challenges = listOf(
                        Challenge.MultipleChoice(
                            id = "c_es_108",
                            lessonId = "lesson_es_1_3",
                            prompt = "How do you ask 'How are you?'",
                            options = listOf("¿Cómo estás?", "¿Dónde estás?", "¿Quién eres?", "¿Qué hora es?"),
                            correctIndex = 0
                        )
                    )
                )
            )

            val unit2Lessons = listOf(
                Lesson(
                    id = "lesson_es_2_1",
                    unitId = "unit_es_2",
                    title = "At the Café",
                    orderIndex = 1,
                    isUnlocked = false,
                    isCompleted = false,
                    challenges = listOf(
                        Challenge.TokenArrange(
                            id = "c_es_201",
                            lessonId = "lesson_es_2_1",
                            prompt = "Translate: 'A coffee, please'",
                            targetTokens = listOf("Un", "café", "por", "favor"),
                            availableTokens = listOf("por", "café", "té", "Un", "favor", "con")
                        )
                    )
                )
            )

            return Course(
                id = "course_es",
                title = "Spanish from English",
                sourceLanguage = "en",
                targetLanguage = "es",
                flagEmoji = "🇪🇸",
                units = listOf(
                    UnitModel(
                        id = "unit_es_1",
                        courseId = "course_es",
                        title = "Unit 1: Essential Greetings",
                        description = "Say hello, introduce yourself, and master polite expressions",
                        orderIndex = 1,
                        lessons = unit1Lessons
                    ),
                    UnitModel(
                        id = "unit_es_2",
                        courseId = "course_es",
                        title = "Unit 2: Cafe & Food",
                        description = "Order coffee, snacks, and pay the bill like a local",
                        orderIndex = 2,
                        lessons = unit2Lessons
                    )
                )
            )
        }
    }
}
