package com.whatsapptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.whatsapptracker.R
import com.whatsapptracker.ui.screens.formatDuration
import com.whatsapptracker.ui.theme.WhatsAppDarkGreen
import com.whatsapptracker.ui.theme.WhatsAppGreen
import com.whatsapptracker.ui.theme.WhatsAppTeal

@Composable
fun TodayTimeCard(durationMs: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(WhatsAppTeal, WhatsAppDarkGreen, WhatsAppGreen)
                    )
                )
                .padding(horizontal = 28.dp, vertical = 36.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.screen_time),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = formatDuration(durationMs),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.on_whatsapp_today),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
    }
}
