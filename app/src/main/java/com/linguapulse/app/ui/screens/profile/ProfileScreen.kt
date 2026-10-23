package com.linguapulse.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.ui.theme.PulseBackground
import com.linguapulse.app.ui.theme.PulseBlue
import com.linguapulse.app.ui.theme.PulseGold
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseRed
import com.linguapulse.app.ui.theme.PulseSurface
import com.linguapulse.app.ui.theme.PulseText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val progress = state.userProgress

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Learner Profile",
                        fontWeight = FontWeight.Bold,
                        color = PulseText
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // User Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PulseGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🦉", fontSize = 32.sp)
                    }
                    Column {
                        Text(
                            text = "Language Explorer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PulseText
                        )
                        Text(
                            text = "Joined LinguaPulse • Spanish",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Text(
                text = "STATISTICS",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            // 2x2 Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    iconColor = PulseGold,
                    value = "${progress.streakState.currentStreak}",
                    label = "Day Streak",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Star,
                    iconColor = PulseGreen,
                    value = "${progress.totalXp}",
                    label = "Total XP",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.AcUnit,
                    iconColor = PulseBlue,
                    value = "${progress.streakState.streakFreezesAvailable}",
                    label = "Streak Freezes",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Diamond,
                    iconColor = PulseBlue,
                    value = "${progress.gems}",
                    label = "Gems",
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "ACHIEVEMENTS",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            AchievementItem(
                icon = Icons.Default.LocalFireDepartment,
                iconColor = PulseGold,
                title = "Wildfire",
                desc = "Reach a 7-day practice streak",
                progressText = "${progress.streakState.currentStreak} / 7"
            )

            AchievementItem(
                icon = Icons.Default.WorkspacePremium,
                iconColor = PulseGreen,
                title = "Sage",
                desc = "Earn 250 XP across all units",
                progressText = "${progress.totalXp} / 250"
            )

            Text(
                text = "PREFERENCES",
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PulseSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Sound Effects", fontWeight = FontWeight.Medium, color = PulseText)
                        Switch(
                            checked = state.soundEffectsEnabled,
                            onCheckedChange = { viewModel.toggleSoundEffects() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseGreen, checkedTrackColor = Color(0xFFD7FFB8))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Haptic Vibration", fontWeight = FontWeight.Medium, color = PulseText)
                        Switch(
                            checked = state.hapticFeedbackEnabled,
                            onCheckedChange = { viewModel.toggleHapticFeedback() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PulseGreen, checkedTrackColor = Color(0xFFD7FFB8))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PulseSurface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PulseText
            )
            Text(
                text = label,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AchievementItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String,
    progressText: String,
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
                modifier = Modifier.size(36.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PulseText
                )
                Text(
                    text = desc,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = progressText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = PulseGreen
            )
        }
    }
}
