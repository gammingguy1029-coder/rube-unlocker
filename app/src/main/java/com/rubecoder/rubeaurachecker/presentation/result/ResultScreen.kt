package com.rubecoder.rubeaurachecker.presentation.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraButton
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraOutlineButton
import com.rubecoder.rubeaurachecker.domain.util.AuraScoreTiers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Int,
    onScanAgain: () -> Unit,
    onHome: () -> Unit
) {
    val isPerfectAura = score == 999
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "pulse"
    )
    val glowAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    RubeAuraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)
                .drawBehind {
                    if (isPerfectAura) {
                        drawPerfectAuraEffect(pulse, glowAlpha)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Result score with animation
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                        .scaleY(if (isPerfectAura) pulse else 1f)
                        .scaleX(if (isPerfectAura) pulse else 1f)
                ) {
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayLarge,
                        color = if (isPerfectAura) NeonRed else NeonWhite,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Aura score label
                Text(
                    text = stringResource(R.string.aura_score_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonWhite,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Description based on tier
                val tier = AuraScoreTiers.getTierForScore(score)
                Text(
                    text = tier.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NeonWhite,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Special message for perfect aura
                if (isPerfectAura) {
                    Text(
                        text = stringResource(R.string.perfect_aura_message),
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonRed,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    RubeAuraButton(
                        text = stringResource(R.string.scan_again),
                        onClick = onScanAgain,
                        isElevated = true,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    RubeAuraButton(
                        text = stringResource(R.string.go_home),
                        onClick = onHome,
                        isElevated = true,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Share button (placeholder)
                RubeAuraOutlineButton(
                    text = stringResource(R.string.share_result),
                    onClick = { /* TODO: implement share */ },
                )
            }
        }
    }
}

/** Draws the perfect aura effect: expanding rings and pulsating glow. */
private fun DrawScope.drawPerfectAuraEffect(pulse: Float, glowAlpha: Float) {
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    val maxRadius = this.size.minDimension * 0.4f

    // Outer expanding rings
    repeat(3) { i ->
        val radius = maxRadius * (0.5f + i * 0.2f) * pulse
        val alpha = glowAlpha * (1f - i * 0.3f)
        drawCircle(
            color = NeonRed.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = Stroke(width = 3f),
        )
    }

    // Inner pulsating core
    val coreRadius = maxRadius * 0.3f * pulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonRed.copy(alpha = 0.8f),
                NeonRed.copy(alpha = 0.3f),
                Color.Transparent,
            ),
            center = center,
            radius = coreRadius,
        ),
        radius = coreRadius,
        center = center,
    )
}