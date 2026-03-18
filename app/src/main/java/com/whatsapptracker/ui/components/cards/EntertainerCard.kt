package com.whatsapptracker.ui.components.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.R
import com.whatsapptracker.data.model.YearlyReportData

@Composable
fun EntertainerCard(data: YearlyReportData, isVisible: Boolean) {
    val entertainer = data.topEntertainer
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (entertainer != null) {
            Text(
                text = stringResource(R.string.entertainer_title),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = alpha * 0.9f),
                fontWeight = FontWeight.Black,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.entertainer_emoji),
                fontSize = 64.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.entertainer_watched),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            Text(
                text = entertainer.contactName,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.entertainer_status_most),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            val hours = entertainer.totalDuration / 3600000
            val minutes = (entertainer.totalDuration % 3600000) / 60000
            val seconds = (entertainer.totalDuration % 60000) / 1000
            
            val durationText = when {
                hours > 0 -> stringResource(R.string.entertainer_duration_h_m, hours, minutes)
                minutes > 0 -> stringResource(R.string.entertainer_duration_m_s, minutes, seconds)
                else -> stringResource(R.string.entertainer_duration_s, seconds)
            }
            
            Text(
                text = durationText,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.9f),
                fontWeight = FontWeight.Bold,
            )
        } else {
            Text(
                text = stringResource(R.string.entertainer_empty),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}
