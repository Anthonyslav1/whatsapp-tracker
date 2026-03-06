package com.whatsapptracker.ui.components.cards

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOut
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
import com.whatsapptracker.R
import com.whatsapptracker.data.repository.YearlyReportData

@Composable
fun TopFiveCard(data: YearlyReportData, isVisible: Boolean) {
    val top5 = data.topContacts.take(5)
    val maxDuration = top5.firstOrNull()?.totalDuration?.toFloat() ?: 1f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.top_five_title),
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(32.dp))

        top5.forEachIndexed { index, contact ->
            val fraction = (contact.totalDuration / maxDuration).coerceIn(0.1f, 1f)
            val animatedFraction by animateFloatAsState(
                targetValue = if (isVisible) fraction else 0f,
                animationSpec = tween(800, delayMillis = index * 150, easing = EaseOut),
                label = "bar$index"
            )
            val hours = contact.totalDuration / 3600000
            val minutes = (contact.totalDuration % 3600000) / 60000

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. ${contact.contactName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.top_five_time, hours, minutes),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (top5.isEmpty()) {
            Text(
                text = stringResource(R.string.top_five_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}
