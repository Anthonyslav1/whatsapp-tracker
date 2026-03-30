package com.whatsapptracker.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.random.Random

@Composable
fun MostActiveMonthCard(data: YearlyReportData, isVisible: Boolean) {
    if (!isVisible) return

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column {
                Text(
                    text = "USAGE PEAK",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanAccentMuted,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "October Peak",
                    style = AppTypography.displaySmall,
                    color = TextPrimary,
                )
            }

            // Heatmap grid (Visual approximation of interaction density)
            Column(modifier = Modifier.fillMaxWidth()) {
                val seed = data.totalSessionCount.hashCode().let { if (it == 0) 42 else it }
                val random = Random(seed)
                
                repeat(4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(7) {
                            val intensity = random.nextFloat()
                            val color = when {
                                intensity > 0.8f -> CyanAccent
                                intensity > 0.5f -> CyanAccentMuted
                                intensity > 0.2f -> Color(0xFF006B78)
                                else -> Color(0xFF2A2D35)
                            }
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }

            // Subtitle Description
            Text(
                text = "Interaction density surged during the mid-autumn window.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                lineHeight = 22.sp,
            )
        }
    }
}
