package com.rubecoder.rubeaurachecker.presentation.scanner

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.InfiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import com.rubecoder.rubeaurachecker.R
import com.rubecoder.rubeaurachecker.core.theme.RubeAuraTheme
import com.rubecoder.rubeaurachecker.core.theme.NeonRed
import com.rubecoder.rubeaurachecker.core.theme.NeonWhite
import com.rubecoder.rubeaurachecker.core.theme.PureBlack
import com.rubecoder.rubeaurachecker.core.ui.RubeAuraButton
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onScanComplete: (Int) -> Unit,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate to the result screen as soon as a scan finishes.
    LaunchedEffect(uiState.score) {
        uiState.score?.let(onScanComplete)
    }

    // Animations for the scanner (only active when scanning)
    val sweepAngle by animateFloatAsState(
        targetValue = if (uiState.isScanning) 360f else 0f,
        animationSpec = if (uiState.isScanning)
            InfiniteRepeatable(tween(2000), RepeatMode.Restart)
        else
            tween(0) // Immediately reset when not scanning
    )
    val pulse by animateFloatAsState(
        targetValue = if (uiState.isScanning) 1.1f else 1f,
        animationSpec = if (uiState.isScanning)
            InfiniteRepeatable(tween(800), RepeatMode.Reverse)
        else
            tween(0) // Immediately reset when not scanning
    )

    RubeAuraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PureBlack)
                .drawBehind {
                    if (uiState.isScanning) {
                        drawScannerRadar(uiState.progress, sweepAngle)
                        drawEnergyParticles(uiState.progress, pulse)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Top bar (always shown)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            viewModel.onCancelClicked()
                            onGoBack()
                        },
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = NeonWhite,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.scanner_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main content (changes with state)
                when {
                    uiState.isScanning -> {
                        // Scanning: show a message and the radar (already in background)
                        Text(
                            text = stringResource(R.string.scanning),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeonWhite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        )
                    }
                    uiState.score != null -> {
                        // Result: show the score
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${uiState.score}",
                                style = MaterialTheme.typography.displayLarge,
                                color = NeonRed,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(R.string.aura_score_label),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeonWhite,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    else -> {
                        // Ready: show a prompt
                        Text(
                            text = stringResource(R.string.ready_to_scan),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeonWhite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons (changes with state)
                when {
                    uiState.isScanning -> {
                        // Show cancel button
                        RubeAuraButton(
                            text = stringResource(R.string.cancel),
                            onClick = {
                                viewModel.onCancelClicked()
                                onGoBack()
                            },
                            isElevated = false,
                        )
                    }
                    uiState.score != null -> {
                        // Show score again and go home buttons
                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RubeAuraButton(
                                text = stringResource(R.string.scan_again),
                                onClick = {
                                    viewModel.onScanClicked()
                                },
                                isElevated = true,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            RubeAuraButton(
                                text = stringResource(R.string.go_home),
                                onClick = { onGoBack() },
                                isElevated = true,
                            )
                        }
                    }
                    else -> {
                        // Show start scan button
                        RubeAuraButton(
                            text = stringResource(R.string.start_scan),
                            onClick = {
                                viewModel.onScanClicked()
                            },
                            isElevated = true,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                }
            }
        }
    }
}

/** Draws the radar rings and sweep line for the scanner. */
private fun DrawScope.drawScannerRadar(progress: Float, sweepAngle: Float) {
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    val maxRadius = this.size.minDimension * 0.4f

    // Draw three radar rings that expand and fade based on progress.
    // We'll make them static (not expanding) but change their alpha based on progress.
    // We'll make three rings at fixed radii: 0.2, 0.4, 0.6 of maxRadius.
    val ringRadii = listOf(maxRadius * 0.2, maxRadius * 0.4, maxRadius * 0.6)
    for (radius in ringRadii) {
        // The alpha is highest when progress is 0 and fades as progress increases.
        val alpha = (1f - progress).coerceIn(0f, 1f) * 0.5f
        drawCircle(
            color = NeonRed.copy(alpha = alpha),
            radius = radius,
            center = center,
            style = Stroke(width = 2f),
        )
    }

    // Draw the sweep line that rotates and shows the progress as an arc.
    // We'll draw an arc from 0 to (progress * 360) degrees.
    val sweepProgress = (progress * 360).coerceIn(0f, 360f)
    drawArc(
        color = NeonRed.copy(alpha = 0.7f),
        startAngle = -90f, // Start from the top
        sweepAngle = sweepProgress,
        useCenter = true,
        topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
        size = Size(maxRadius * 2, maxRadius * 2),
    )
}

/** Draws energy particles that move outward and fade. */
private fun DrawScope.drawEnergyParticles(progress: Float, pulse: Float) {
    val center = Offset(this.size.width / 2f, this.size.height / 2f)
    val maxRadius = this.size.minDimension * 0.4f

    // We'll draw a fixed number of particles (e.g., 20) that are randomly placed
    // and then move outward based on the progress and pulse.
    val particleCount = 20
    val particleSize = 2.dp
    for (i in 0 until particleCount) {
        // Each particle has a random angle and a base distance from the center.
        val angle = (i * 360 / particleCount).toFloat() * (Math.PI / 180)
        val baseDistance = maxRadius * 0.5f // Start at half the radius
        // The distance increases with progress and pulse.
        val distance = baseDistance * progress * pulse
        val particleOffset = Offset(
            center.x + distance * cos(angle),
            center.y + distance * sin(angle)
        )
        // The alpha is based on the pulse and progress.
        val alpha = (pulse - 0.9f) * 2.5f // Maps 0.9-1.1 to 0-0.5
        drawCircle(
            color = NeonRed.copy(alpha = alpha.coerceIn(0f, 0.5f)),
            radius = particleSize.value / 2,
            center = particleOffset,
        )
    }
}

private fun cos(angleInRadians: Float): Double = Math.cos(angleInRadians.toDouble())
private fun sin(angleInRadians: Float): Double = Math.sin(angleInRadians.toDouble())