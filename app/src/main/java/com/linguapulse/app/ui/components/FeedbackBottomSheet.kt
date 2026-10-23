package com.linguapulse.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.domain.model.ValidationResult
import com.linguapulse.app.ui.theme.PulseGold
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseRed

@Composable
fun FeedbackBottomSheet(
    result: ValidationResult?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = result != null,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        if (result == null) return@AnimatedVisibility

        val (backgroundColor, icon, title, subtitle) = when (result) {
            is ValidationResult.ExactMatch -> Quad(
                Color(0xFFD7FFB8),
                Icons.Default.CheckCircle,
                PulseGreen,
                "Nicely done!"
            )
            is ValidationResult.TypoTolerated -> Quad(
                Color(0xFFFFF4D0),
                Icons.Default.Info,
                PulseGold,
                "Almost there! Watch for minor typos: ${result.suggestedSolution}"
            )
            is ValidationResult.Incorrect -> Quad(
                Color(0xFFFFDFE0),
                Icons.Default.Close,
                PulseRed,
                "Correct solution:\n${result.correctSolution}"
            )
            is ValidationResult.EmptySubmission -> Quad(
                Color(0xFFE5E5E5),
                Icons.Default.Info,
                Color.Gray,
                "Please provide an answer before checking."
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = title,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = when (result) {
                        is ValidationResult.ExactMatch -> "Nicely done!"
                        is ValidationResult.TypoTolerated -> "Typo Detected"
                        is ValidationResult.Incorrect -> "Incorrect"
                        is ValidationResult.EmptySubmission -> "Empty Input"
                    },
                    color = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                color = Color(0xFF4B4B4B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            PulseButton(
                text = "Continue",
                onClick = onContinue,
                style = when (result) {
                    is ValidationResult.ExactMatch -> PulseButtonStyle.PRIMARY
                    is ValidationResult.TypoTolerated -> PulseButtonStyle.PRIMARY
                    is ValidationResult.Incorrect -> PulseButtonStyle.DANGER
                    is ValidationResult.EmptySubmission -> PulseButtonStyle.NEUTRAL
                }
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
