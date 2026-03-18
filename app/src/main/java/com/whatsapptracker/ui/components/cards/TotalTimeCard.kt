package com.whatsapptracker.ui.components.cards

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.R
import com.whatsapptracker.data.model.YearlyReportData

@Composable
fun TotalTimeCard(data: YearlyReportData, isVisible: Boolean) {
    val hours = data.totalDurationMs / 3600000
    val animatedHours by animateIntAsState(
        targetValue = if (isVisible) hours.toInt() else 0,
        animationSpec = tween(1500, easing = EaseOut), label = "hours"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.total_time_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$animatedHours",
            fontSize = 96.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
        )
        Text(
            text = stringResource(R.string.total_time_hours),
            style = MaterialTheme.typography.displaySmall,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.total_time_subtitle),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Fun comparison
        val movies = (hours / 2).coerceAtLeast(1)
        Text(
            text = stringResource(R.string.total_time_movies, movies),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
        )
    }
}
