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
fun BestFriendCard(data: YearlyReportData, isVisible: Boolean) {
    val bestFriend = data.topContacts.firstOrNull()
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (bestFriend != null) {
            Text(
                text = "MOST TIME SPENT WITH",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.8f),
            )
            Text(
                text = "LONGEST CHATTER",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = alpha * 0.8f),
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.best_friend_emoji),
                fontSize = 56.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = bestFriend.contactName,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            val hours = bestFriend.totalDuration / 3600000
            val minutes = (bestFriend.totalDuration % 3600000) / 60000
            Text(
                text = stringResource(R.string.best_friend_time, hours, minutes),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            if (data.totalDurationMs > 0) {
                val pct = (bestFriend.totalDuration * 100 / data.totalDurationMs).toInt()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.best_friend_pct, pct.toString()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = alpha * 0.5f),
                )
            }
        } else {
            Text(
                text = stringResource(R.string.no_chats),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}
