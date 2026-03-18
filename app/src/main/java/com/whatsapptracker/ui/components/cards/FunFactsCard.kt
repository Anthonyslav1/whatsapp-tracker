package com.whatsapptracker.ui.components.cards

import androidx.compose.animation.core.animateFloatAsState
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
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FunFactsCard(data: YearlyReportData, isVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.fun_facts_title),
            style = MaterialTheme.typography.displaySmall,
            color = Color.White.copy(alpha = alpha),
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Unique contacts
        FunFactItem(
            emoji = "👥",
            text = stringResource(R.string.fun_facts_contacts, data.uniqueContactCount.toString()),
            alpha = alpha
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Longest session
        data.longestSession?.let { session ->
            val hours = session.durationMs / 3600000
            val minutes = (session.durationMs % 3600000) / 60000
            FunFactItem(
                emoji = "⏱️",
                text = stringResource(R.string.fun_facts_longest, hours, minutes, session.contactName),
                alpha = alpha
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Most active day
        data.mostActiveDayOfWeek?.let { day ->
            FunFactItem(
                emoji = "📅",
                text = stringResource(R.string.fun_facts_active_day, day.getDisplayName(TextStyle.FULL, Locale.getDefault())),
                alpha = alpha
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Total sessions
        FunFactItem(
            emoji = "💬",
            text = stringResource(R.string.fun_facts_sessions, data.totalSessionCount.toString()),
            alpha = alpha
        )
    }
}

@Composable
private fun FunFactItem(emoji: String, text: String, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = alpha * 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = alpha * 0.9f),
            fontWeight = FontWeight.Medium,
        )
    }
}
