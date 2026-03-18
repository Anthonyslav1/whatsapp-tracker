package com.whatsapptracker.ui.components.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.R
import com.whatsapptracker.data.model.YearlyReportData
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MostActiveMonthCard(data: YearlyReportData, isVisible: Boolean) {
    val monthlyData = data.monthlyDurations
    val mostActive = monthlyData.maxByOrNull { it.second }
    val maxDuration = mostActive?.second?.toFloat() ?: 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.most_active_month_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (mostActive != null) {
            Text(
                text = mostActive.first.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
            )
            val hours = mostActive.second / 3600000
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.most_active_month_time, hours.toString()),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.7f),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mini month chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Month.entries.forEach { month ->
                val duration = monthlyData.find { it.first == month }?.second ?: 0L
                val fraction = (duration.toFloat() / maxDuration).coerceIn(0f, 1f)
                val barHeight = (80.dp * fraction).coerceAtLeast(2.dp)
                val isMostActive = month == mostActive?.first

                val animatedHeight by animateDpAsState(
                    targetValue = if (isVisible) barHeight else 2.dp,
                    animationSpec = tween(600, delayMillis = month.ordinal * 50),
                    label = "mBar"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(
                                if (isMostActive) Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = month.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        fontSize = 8.sp,
                        color = if (isMostActive) Color.White else Color.White.copy(alpha = 0.4f),
                        fontWeight = if (isMostActive) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}
