package com.whatsapptracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.ui.theme.CyanAccent
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextMuted
import com.whatsapptracker.ui.theme.TextPrimary

@Composable
fun TodayTimeCard(durationMs: Long) {
    // Arbitrary daily cap for the 74% Capacity feel (e.g., 8 hours = 100%)
    val dailyCapMs = 8 * 60 * 60 * 1000L
    val capacityFraction = (durationMs.toFloat() / dailyCapMs).coerceIn(0f, 1f)

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(capacityFraction) {
        animationProgress.animateTo(
            targetValue = capacityFraction,
            animationSpec = tween(1800, easing = EaseOutQuart)
        )
    }

    val totalHours = durationMs / 3600000.0
    val formattedHours = String.format("%.1f", totalHours)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "CURRENT SESSION",
                    color = CyanAccent,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Today's Usage",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Normal,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Big Number
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {
                        contentDescription = "$formattedHours hours"
                    }
            ) {
                Text(
                    text = formattedHours,
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary,
                )
                Text(
                    text = " hours",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent.copy(alpha = 0.8f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("View Deep Logs", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export Data", color = CyanAccent)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Circular Capacity Chart
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Track Background
                    drawArc(
                        color = Color(0xFF15181E),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )

                    // Track Foreground Progress
                    drawArc(
                        color = CyanAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * animationProgress.value,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Inner Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animationProgress.value * 100).toInt()}%",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                    )
                    Text(
                        text = "CAPACITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 2.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
