package com.linguapulse.app.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.ui.components.HeartCounter
import com.linguapulse.app.ui.components.PulseButton
import com.linguapulse.app.ui.components.PulseButtonStyle
import com.linguapulse.app.ui.theme.PulseBackground
import com.linguapulse.app.ui.theme.PulseBlue
import com.linguapulse.app.ui.theme.PulseGold
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseRed
import com.linguapulse.app.ui.theme.PulseSurface
import com.linguapulse.app.ui.theme.PulseText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel,
    onStartPracticeSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Practice & Review",
                        fontWeight = FontWeight.Bold,
                        color = PulseText
                    )
                },
                actions = {
                    HeartCounter(hearts = state.heartsRemaining, maxHearts = state.maxHearts)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseSurface)
            )
        },
        containerColor = PulseBackground,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Heart Refill Callout Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.heartsRemaining < state.maxHearts) Color(0xFFFFDFE0) else Color(0xFFD7FFB8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = if (state.heartsRemaining < state.maxHearts) PulseRed else PulseGreen,
                            modifier = Modifier.size(40.dp)
                        )
                        Column {
                            Text(
                                text = if (state.heartsRemaining < state.maxHearts) "Recover Hearts for Free" else "Hearts are Full!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = PulseText
                            )
                            Text(
                                text = "Practice sessions do not cost hearts when you make mistakes, and reward +1 Heart upon completion.",
                                fontSize = 14.sp,
                                color = PulseText.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Text(
                    text = "PRACTICE MODALITIES",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )

                PracticeOptionCard(
                    icon = Icons.Default.Psychology,
                    iconColor = PulseBlue,
                    title = "Weak Vocabulary Review",
                    subtitle = "${state.weakWordsCount} words need strengthening"
                )

                PracticeOptionCard(
                    icon = Icons.Default.Hearing,
                    iconColor = PulseGold,
                    title = "Listening & Pronunciation",
                    subtitle = "Tune your ear to native cadence"
                )

                PracticeOptionCard(
                    icon = Icons.Default.FitnessCenter,
                    iconColor = PulseGreen,
                    title = "Speed Challenge",
                    subtitle = "Quick-fire translation drill"
                )
            }

            PulseButton(
                text = "Start Practice (+1 Heart)",
                onClick = onStartPracticeSession,
                style = PulseButtonStyle.PRIMARY
            )
        }
    }
}

@Composable
fun PracticeOptionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PulseText
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
