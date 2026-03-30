package com.whatsapptracker.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.data.model.YearlyReportData
import com.whatsapptracker.ui.theme.AppTypography
import com.whatsapptracker.ui.theme.CyanAccent
import com.whatsapptracker.ui.theme.CyanAccentMuted
import com.whatsapptracker.ui.theme.TextPrimary
import com.whatsapptracker.ui.theme.TextSecondary

@Composable
fun BestFriendCard(data: YearlyReportData, isVisible: Boolean) {
    if (!isVisible) return

    val bestFriend = data.topRelationshipContacts.firstOrNull()
    val name = bestFriend?.contactName ?: "The Void"
    val hours = formatDurationHours(bestFriend?.totalDuration ?: 0L)

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fake Background Image Gradient (grayscale)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2E323A), Color(0xFF16181D))
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column {
                    Text(
                        text = "YOUR #1 BEST FRIEND",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccentMuted,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = name.replace(" ", "\n"),
                        style = AppTypography.displayMedium,
                        color = TextPrimary,
                        lineHeight = 44.sp,
                    )
                }

                // Data Box & Heart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF22252D))
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Column {
                            Text(
                                text = hours,
                                style = AppTypography.displaySmall,
                                color = CyanAccent,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "HOURS TOGETHER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart",
                        tint = CyanAccent,
                        modifier = Modifier.size(36.dp).padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

private fun formatDurationHours(durationMs: Long): String {
    val hours = durationMs / 3600000
    return if (hours > 0) "$hours" else "<1"
}
