package com.whatsapptracker.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.ui.screens.formatDurationShort
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextMuted
import com.whatsapptracker.ui.theme.WhatsAppGreen
import com.whatsapptracker.ui.theme.WhatsAppTeal
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyChart(dailyTotals: Map<LocalDate, Long>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val maxDuration = dailyTotals.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val chartHeight = 120.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { date ->
                val duration = dailyTotals[date] ?: 0L
                val fraction = (duration.toFloat() / maxDuration).coerceIn(0f, 1f)
                val barHeight = (chartHeight * fraction).coerceAtLeast(4.dp)
                val isToday = date == today

                val animatedHeight by animateDpAsState(
                    targetValue = barHeight,
                    animationSpec = tween(600, easing = EaseOut),
                    label = "barH"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Duration label on top
                    if (duration > 0) {
                        Text(
                            text = formatDurationShort(duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (isToday) Brush.verticalGradient(
                                    listOf(WhatsAppGreen, WhatsAppTeal)
                                )
                                else Brush.verticalGradient(
                                    listOf(
                                        WhatsAppGreen.copy(alpha = 0.5f),
                                        WhatsAppTeal.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ).take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) WhatsAppGreen else TextMuted,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}
