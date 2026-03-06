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
import com.whatsapptracker.data.repository.YearlyReportData

@Composable
fun IntroCard(data: YearlyReportData, isVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.intro_emoji),
            fontSize = 64.sp,
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.intro_title),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White.copy(alpha = alpha),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            lineHeight = 52.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.year}",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White.copy(alpha = alpha * 0.6f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.intro_swipe),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = alpha * 0.5f),
        )
    }
}
