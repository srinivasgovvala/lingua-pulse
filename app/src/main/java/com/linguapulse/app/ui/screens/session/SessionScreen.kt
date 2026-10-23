package com.linguapulse.app.ui.screens.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.domain.model.Challenge
import com.linguapulse.app.ui.components.FeedbackBottomSheet
import com.linguapulse.app.ui.components.HeartCounter
import com.linguapulse.app.ui.components.PulseButton
import com.linguapulse.app.ui.components.PulseButtonStyle
import com.linguapulse.app.ui.components.PulseProgressBar
import com.linguapulse.app.ui.theme.PulseBackground
import com.linguapulse.app.ui.theme.PulseBlue
import com.linguapulse.app.ui.theme.PulseGold
import com.linguapulse.app.ui.theme.PulseGrayLight
import com.linguapulse.app.ui.theme.PulseGrayMedium
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseRed
import com.linguapulse.app.ui.theme.PulseSurface
import com.linguapulse.app.ui.theme.PulseText

@Composable
fun SessionScreen(
    viewModel: SessionViewModel,
    onFinishSession: () -> Unit,
    onNavigateToReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PulseSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onFinishSession,
                    modifier = Modifier.semantics { contentDescription = "Exit lesson" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = PulseGrayMedium
                    )
                }

                PulseProgressBar(
                    progress = state.progressFraction,
                    modifier = Modifier.weight(1f)
                )

                HeartCounter(hearts = state.heartsRemaining)
            }
        },
        bottomBar = {
            FeedbackBottomSheet(
                result = state.validationResult,
                onContinue = { viewModel.continueNext() }
            )
        },
        containerColor = PulseBackground,
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PulseGreen)
                    }
                }

                state.isSessionComplete -> {
                    SessionCompleteView(
                        earnedXp = state.earnedXp,
                        maxCombo = state.maxCombo,
                        onContinue = onFinishSession
                    )
                }

                state.isOutOfHearts -> {
                    OutOfHeartsView(
                        onGoToPractice = onNavigateToReview,
                        onExit = onFinishSession
                    )
                }

                state.activeChallenge != null -> {
                    ChallengeContentView(
                        challenge = state.activeChallenge!!,
                        isEvaluating = state.validationResult != null,
                        onSubmit = { submission -> viewModel.submitAnswer(submission) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChallengeContentView(
    challenge: Challenge,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "QUESTION",
                color = PulseGrayMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = challenge.prompt,
                color = PulseText,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (challenge) {
                is Challenge.MultipleChoice -> {
                    MultipleChoiceView(
                        challenge = challenge,
                        isEvaluating = isEvaluating,
                        onSubmit = onSubmit
                    )
                }

                is Challenge.TokenArrange -> {
                    TokenArrangeView(
                        challenge = challenge,
                        isEvaluating = isEvaluating,
                        onSubmit = onSubmit
                    )
                }

                is Challenge.TextInput -> {
                    TextInputView(
                        challenge = challenge,
                        isEvaluating = isEvaluating,
                        onSubmit = onSubmit
                    )
                }

                is Challenge.PairMatching -> {
                    PairMatchingView(
                        challenge = challenge,
                        isEvaluating = isEvaluating,
                        onSubmit = onSubmit
                    )
                }

                is Challenge.Listening -> {
                    ListeningView(
                        challenge = challenge,
                        isEvaluating = isEvaluating,
                        onSubmit = onSubmit
                    )
                }
            }
        }
    }
}

@Composable
fun MultipleChoiceView(
    challenge: Challenge.MultipleChoice,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit
) {
    var selectedIndex by remember(challenge.id) { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        challenge.options.forEachIndexed { index, option ->
            val isSelected = selectedIndex == index
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFDDF4FF) else PulseSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = if (isSelected) PulseBlue else PulseGrayLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 60.dp)
                    .clickable(enabled = !isEvaluating) { selectedIndex = index }
                    .semantics { contentDescription = "Option ${index + 1}: $option" }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) PulseBlue else PulseGrayMedium,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else PulseGrayLight)
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = option,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = PulseText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PulseButton(
            text = "Check Answer",
            enabled = selectedIndex != null && !isEvaluating,
            onClick = { selectedIndex?.let { onSubmit(it) } },
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TokenArrangeView(
    challenge: Challenge.TokenArrange,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit
) {
    val selectedTokens = remember(challenge.id) { mutableStateListOf<String>() }
    val availableTokens = remember(challenge.id) { mutableStateListOf<String>().apply { addAll(challenge.availableTokens) } }

    Column {
        // Assembly Slot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PulseSurface)
                .border(2.dp, PulseGrayLight, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (selectedTokens.isEmpty()) {
                Text(
                    text = "Tap word chips below to build the sentence",
                    color = PulseGrayMedium,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedTokens.forEachIndexed { index, token ->
                        WordChip(
                            text = token,
                            onClick = {
                                if (!isEvaluating) {
                                    selectedTokens.removeAt(index)
                                    availableTokens.add(token)
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Word Bank
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            availableTokens.forEachIndexed { index, token ->
                WordChip(
                    text = token,
                    onClick = {
                        if (!isEvaluating) {
                            availableTokens.removeAt(index)
                            selectedTokens.add(token)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PulseButton(
            text = "Check Answer",
            enabled = selectedTokens.isNotEmpty() && !isEvaluating,
            onClick = { onSubmit(selectedTokens.toList()) },
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@Composable
fun WordChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PulseGrayLight)
            .padding(bottom = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PulseText
        )
    }
}

@Composable
fun TextInputView(
    challenge: Challenge.TextInput,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit
) {
    var text by remember(challenge.id) { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { if (!isEvaluating) text = it },
            placeholder = { Text("Type in Spanish...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            enabled = !isEvaluating
        )

        Spacer(modifier = Modifier.height(32.dp))

        PulseButton(
            text = "Check Answer",
            enabled = text.isNotBlank() && !isEvaluating,
            onClick = { onSubmit(text) },
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@Composable
fun PairMatchingView(
    challenge: Challenge.PairMatching,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit
) {
    val matched = remember(challenge.id) { mutableStateMapOf<String, String>() }
    var selectedLeft by remember(challenge.id) { mutableStateOf<String?>(null) }

    val leftItems = remember(challenge.id) { challenge.pairs.keys.toList() }
    val rightItems = remember(challenge.id) { challenge.pairs.values.shuffled() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leftItems.forEach { left ->
                    val isMatched = matched.containsKey(left)
                    val isSelected = selectedLeft == left

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isMatched -> Color(0xFFD7FFB8)
                                isSelected -> Color(0xFFDDF4FF)
                                else -> PulseSurface
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clickable(enabled = !isMatched && !isEvaluating) {
                                selectedLeft = left
                            }
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = left,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatched) PulseGreen else PulseText
                            )
                        }
                    }
                }
            }

            // Right Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rightItems.forEach { right ->
                    val isMatched = matched.containsValue(right)

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMatched) Color(0xFFD7FFB8) else PulseSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 48.dp)
                            .clickable(enabled = !isMatched && selectedLeft != null && !isEvaluating) {
                                selectedLeft?.let { left ->
                                    if (challenge.pairs[left] == right) {
                                        matched[left] = right
                                        selectedLeft = null
                                    } else {
                                        selectedLeft = null
                                    }
                                }
                            }
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = right,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatched) PulseGreen else PulseText
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PulseButton(
            text = "Check Pairs",
            enabled = matched.size == challenge.pairs.size && !isEvaluating,
            onClick = { onSubmit(matched.toMap()) },
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@Composable
fun ListeningView(
    challenge: Challenge.Listening,
    isEvaluating: Boolean,
    onSubmit: (Any) -> Unit
) {
    var text by remember(challenge.id) { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PulseBlue),
            modifier = Modifier
                .size(80.dp)
                .clickable { /* Audio playback triggered */ }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "🔊", fontSize = 36.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { if (!isEvaluating) text = it },
            placeholder = { Text("Type what you heard...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            enabled = !isEvaluating
        )

        Spacer(modifier = Modifier.height(32.dp))

        PulseButton(
            text = "Check Answer",
            enabled = text.isNotBlank() && !isEvaluating,
            onClick = { onSubmit(text) },
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@Composable
fun SessionCompleteView(
    earnedXp: Int,
    maxCombo: Int,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = PulseGold,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lesson Complete!",
            color = PulseGold,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Great work! You're making continuous progress.",
            color = PulseText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4D0)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "TOTAL XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "+$earnedXp", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PulseGold)
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD7FFB8)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "MAX COMBO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$maxCombo", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PulseGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        PulseButton(
            text = "Continue",
            onClick = onContinue,
            style = PulseButtonStyle.PRIMARY
        )
    }
}

@Composable
fun OutOfHeartsView(
    onGoToPractice: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "💔", fontSize = 72.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Out of Hearts!",
            color = PulseRed,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Practice past lessons to earn hearts back and continue your learning journey without waiting.",
            color = PulseText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        PulseButton(
            text = "Practice to Earn +1 Heart",
            onClick = onGoToPractice,
            style = PulseButtonStyle.PRIMARY
        )

        Spacer(modifier = Modifier.height(12.dp))

        PulseButton(
            text = "Exit to Home",
            onClick = onExit,
            style = PulseButtonStyle.NEUTRAL
        )
    }
}
