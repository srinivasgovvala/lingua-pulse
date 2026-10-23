package com.linguapulse.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.linguapulse.app.ui.theme.PulseBlue
import com.linguapulse.app.ui.theme.PulseBlueDark
import com.linguapulse.app.ui.theme.PulseGrayLight
import com.linguapulse.app.ui.theme.PulseGrayMedium
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseGreenDark
import com.linguapulse.app.ui.theme.PulseRed
import com.linguapulse.app.ui.theme.PulseRedDark

enum class PulseButtonStyle {
    PRIMARY, SECONDARY, DANGER, NEUTRAL
}

@Composable
fun PulseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: PulseButtonStyle = PulseButtonStyle.PRIMARY
) {
    val (faceColor, shadowColor, textColor) = when (style) {
        PulseButtonStyle.PRIMARY -> if (enabled) Triple(PulseGreen, PulseGreenDark, Color.White) else Triple(PulseGrayLight, PulseGrayMedium, PulseGrayMedium)
        PulseButtonStyle.SECONDARY -> if (enabled) Triple(PulseBlue, PulseBlueDark, Color.White) else Triple(PulseGrayLight, PulseGrayMedium, PulseGrayMedium)
        PulseButtonStyle.DANGER -> if (enabled) Triple(PulseRed, PulseRedDark, Color.White) else Triple(PulseGrayLight, PulseGrayMedium, PulseGrayMedium)
        PulseButtonStyle.NEUTRAL -> if (enabled) Triple(Color.White, PulseGrayLight, PulseGreen) else Triple(PulseGrayLight, PulseGrayMedium, PulseGrayMedium)
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .background(shadowColor)
            .padding(bottom = 4.dp)
            .clip(shape)
            .background(faceColor)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}
