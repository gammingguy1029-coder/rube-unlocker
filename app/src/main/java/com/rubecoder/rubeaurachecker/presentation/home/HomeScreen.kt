package com.rubecoder.rubeaurachecker.presentation.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.SurfaceDeep
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraButton

@Composable
fun HomeScreen(
    onStartScan: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenOwner: () -> Unit,
) {
    RubeAuraTheme {
        val infinite = rememberInfiniteTransition(label = "home")
        val ringPhase by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Restart),
            label = "ringPhase",
        )
        val sweepAngle by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart),
            label = "sweepAngle",
        )
        val pulse by infinite.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
            label = "pulse",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)
                .drawBehind {
                    drawRadar(ringPhase, sweepAngle)
                    drawEnergyCore(size.minDimension * 0.22f * pulse)
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                TopBar(onOpenHistory, onOpenSettings)

                Spacer(Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    color = NeonWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.home_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                Spacer(Modifier.height(180.dp))

                Spacer(Modifier.weight(1f))

                RubeAuraButton(
                    text = stringResource(R.string.start_scan),
                    onClick = onStartScan,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.home_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.weight(1f))

                OwnerTrigger(onOpenOwner)
            }
        }
    }
}

@Composable
private fun TopBar(onOpenHistory: () -> Unit, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onOpenHistory,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Scan history",
                tint = NeonRed,
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = NeonRed,
            )
        }
    }
}

@Composable
private fun OwnerTrigger(onOpenOwner: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clickable(onClick = onOpenOwner),
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(
                    color = NeonRed.copy(alpha = 0.18f),
                    radius = this.size.minDimension / 2,
                )
            }
        }
    }
}

/** Three expanding radar rings centred on the screen. */
private fun DrawScope.drawRadar(ringPhase: Float, sweepAngle: Float) {
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    val maxRadius = this.size.minDimension * 0.45f

    repeat(3) { i ->
        val phase = (ringPhase + i * 0.333f) % 1f
        val radius = maxRadius * phase
        val alpha = (1f - phase).coerceIn(0f, 1f) * 0.5f
        drawCircle(
            color = NeonRed.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = Stroke(width = 2f),
        )
    }

    // Rotating radar sweep line
    rotate(degrees = sweepAngle, pivot = center) {
        drawArc(
            color = NeonRed.copy(alpha = 0.35f),
            startAngle = -12f,
            sweepAngle = 24f,
            useCenter = true,
            topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
            size = Size(maxRadius * 2, maxRadius * 2),
        )
    }
}

/** Radial-gradient energy core. */
private fun DrawScope.drawEnergyCore(radius: Float) {
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                NeonRed.copy(alpha = 0.85f),
                NeonRed.copy(alpha = 0.25f),
                Color.Transparent,
            ),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
    drawCircle(
        color = SurfaceDeep,
        radius = radius * 0.35f,
        center = center,
    )
}