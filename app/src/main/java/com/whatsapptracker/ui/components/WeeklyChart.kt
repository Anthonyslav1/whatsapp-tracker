package com.whatsapptracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.ui.screens.formatDurationShort
import com.whatsapptracker.ui.theme.CyanAccent
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextMuted
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyChart(dailyTotals: Map<LocalDate, Long>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val maxDuration = dailyTotals.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L

    // Extract ordered values
    val values = days.map { dailyTotals[it] ?: 0L }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(dailyTotals) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = EaseInOutCubic)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clearAndSetSemantics {
                val chartDescription = days.joinToString(separator = ", ") { date ->
                    val duration = dailyTotals[date] ?: 0L
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    "$dayName: ${if (duration > 0) formatDurationShort(duration) else "0 minutes"}"
                }
                contentDescription = "Weekly trend chart. $chartDescription"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // The canvas chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val width = size.width
                val height = size.height
                val xStep = width / (values.size - 1).coerceAtLeast(1)

                val points = values.mapIndexed { index, duration ->
                    val fraction = (duration.toFloat() / maxDuration).coerceIn(0f, 1f)
                    val y = height - (fraction * height * animationProgress.value)
                    val x = index * xStep
                    Offset(x, y)
                }

                if (points.isNotEmpty()) {
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }

                    // Create fill path
                    val fillPath = Path().apply {
                        addPath(linePath)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    // Draw Gradient Fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.25f * animationProgress.value),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Line
                    drawPath(
                        path = linePath,
                        color = CyanAccent.copy(alpha = animationProgress.value),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw glowing dots at points
                    points.forEach { point ->
                        if (point.y < height - 1) { // Only draw dots if there's data
                            drawCircle(
                                color = DarkSurfaceVariant,
                                radius = 4.dp.toPx(),
                                center = point
                            )
                            drawCircle(
                                color = CyanAccent.copy(alpha = animationProgress.value),
                                radius = 2.5.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // X-Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                days.forEach { date ->
                    Text(
                        text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ).take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
