package com.whatsapptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.ui.screens.formatDuration
import com.whatsapptracker.ui.theme.CyanAccent
import com.whatsapptracker.ui.theme.CyanAccentMuted
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextSecondary

@Composable
fun EntertainersPillRow(entertainers: List<ContactDuration>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        entertainers.forEachIndexed { index, entertainer ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            ) {
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = if (index == 0)
                                    listOf(CyanAccent, CyanAccentMuted)
                                else
                                    listOf(DarkSurfaceVariant, DarkSurfaceVariant)
                            )
                        )
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = "🍿",
                            fontSize = 24.sp,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = entertainer.contactName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatDuration(entertainer.totalDuration),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (index == 0) Color.White.copy(alpha = 0.8f) else TextSecondary,
                        )
                    }
                }
            }
        }
    }
}
