package com.rubecoder.rubeaurachecker.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonRedFaint
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTypography

/**
 * Primary button with neon red accent on black surface.
 * ponytail: add disabled state, loading indicator, and icon support if needed.
 */
@Composable
fun RubeAuraButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    isElevated: Boolean = true
) {
    val buttonColor = if (enabled) NeonRed else NeonRedFaint
    val contentColor = PureBlack
    val elevation = if (isElevated) 4.dp else 0.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(buttonColor)
            .border(1.dp, NeonRedFaint, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .graphicsLayer {
                elevationY = elevation
            }
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = RubeAuraTypography.bodyLarge.fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(androidx.compose.ui.Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

/**
 * Outline button with neon red border and transparent background.
 */
@Composable
fun RubeAuraOutlineButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) NeonRed else NeonRedFaint
    val contentColor = if (enabled) NeonRed else NeonRedFaint

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Transparent)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = RubeAuraTypography.bodyLarge.fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(androidx.compose.ui.Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}

/**
 * Icon button for actions like back, settings, etc.
 */
@Composable
fun RubeAuraIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val backgroundColor = if (enabled) NeonRedFaint.copy(alpha = 0.2f) else NeonRedFaint.copy(alpha = 0.1f)
    val contentColor = if (enabled) NeonRed else NeonRedFaint

    Box(
        modifier = modifier
            .size(48.dp)
            .background(backgroundColor)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        content()
    }
}