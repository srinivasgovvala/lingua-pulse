package com.linguapulse.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.domain.model.Lesson
import com.linguapulse.app.domain.model.UnitModel
import com.linguapulse.app.ui.components.HeartCounter
import com.linguapulse.app.ui.components.PulseButton
import com.linguapulse.app.ui.components.PulseButtonStyle
import com.linguapulse.app.ui.components.StreakChip
import com.linguapulse.app.ui.theme.PulseBackground
import com.linguapulse.app.ui.theme.PulseBlue
import com.linguapulse.app.ui.theme.PulseGold
import com.linguapulse.app.ui.theme.PulseGoldDark
import com.linguapulse.app.ui.theme.PulseGrayLight
import com.linguapulse.app.ui.theme.PulseGrayMedium
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseGreenDark
import com.linguapulse.app.ui.theme.PulseRed
import com.linguapulse.app.ui.theme.PulseSurface
import com.linguapulse.app.ui.theme.PulseText
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartLesson: (String) -> Unit,
    onStartReview: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.course?.flagEmoji ?: "🌍",
                            fontSize = 24.sp
                        )
                        Text(
                            text = state.course?.title ?: "LinguaPulse",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseText
                        )
                    }
                },
                actions = {
                    StreakChip(
                        streakDays = state.userProgress.streakState.currentStreak,
                        onClick = onNavigateToProfile
                    )
                    HeartCounter(
                        hearts = state.userProgress.heartsState.currentHearts,
                        onClick = onStartReview
                    )
                    if (state.pendingSyncCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .semantics { contentDescription = "${state.pendingSyncCount} offline mutations pending sync" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = PulseBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PulseSurface
                )
            )
        },
        containerColor = PulseBackground,
        modifier = modifier
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PulseGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (state.showHeartsEmptyDialog) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDFE0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "You are out of hearts!",
                                color = PulseRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Practice to regain hearts and continue your lessons.",
                                color = PulseText,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PulseButton(
                                text = "Practice Now (+1 Heart)",
                                onClick = onStartReview,
                                style = PulseButtonStyle.SECONDARY
                            )
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.course?.units?.let { units ->
                        items(units) { unit ->
                            UnitSection(
                                unit = unit,
                                onStartLesson = onStartLesson,
                                heartsRemaining = state.userProgress.heartsState.currentHearts,
                                onOutOfHearts = onStartReview
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitSection(
    unit: UnitModel,
    onStartLesson: (String) -> Unit,
    heartsRemaining: Int,
    onOutOfHearts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Unit Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PulseGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = unit.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = unit.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lesson Path Nodes
        unit.lessons.forEachIndexed { index, lesson ->
            val horizontalOffset = (sin(index.toDouble() * 1.2) * 50).dp

            Box(
                modifier = Modifier
                    .offset(x = horizontalOffset)
                    .padding(vertical = 12.dp)
            ) {
                LessonNode(
                    lesson = lesson,
                    onClick = {
                        if (lesson.isUnlocked) {
                            if (heartsRemaining > 0) {
                                onStartLesson(lesson.id)
                            } else {
                                onOutOfHearts()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LessonNode(
    lesson: Lesson,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, shadowColor, icon, desc) = when {
        lesson.isCompleted -> Quad(PulseGold, PulseGoldDark, Icons.Default.Check, "Completed: ${lesson.title}")
        lesson.isUnlocked -> Quad(PulseGreen, PulseGreenDark, Icons.Default.Star, "Start: ${lesson.title}")
        else -> Quad(PulseGrayLight, PulseGrayMedium, Icons.Default.Lock, "Locked: ${lesson.title}")
    }

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(shadowColor)
            .padding(bottom = 6.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                enabled = lesson.isUnlocked,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (lesson.isUnlocked || lesson.isCompleted) Color.White else PulseGrayMedium,
            modifier = Modifier.size(32.dp)
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
