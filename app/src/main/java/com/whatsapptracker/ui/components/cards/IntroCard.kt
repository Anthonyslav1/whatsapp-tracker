package com.whatsapptracker.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.data.model.YearlyReportData
import com.whatsapptracker.ui.theme.AppTypography
import com.whatsapptracker.ui.theme.CyanAccentMuted
import com.whatsapptracker.ui.theme.TextPrimary
import com.whatsapptracker.ui.theme.TextSecondary

@Composable
fun IntroCard(data: YearlyReportData, isVisible: Boolean) {
    if (!isVisible) return

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "YEARLY REPORT",
            style = MaterialTheme.typography.labelSmall,
            color = CyanAccentMuted,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Twenty\nTwenty Four",
            style = AppTypography.displayLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "A cinematic retrospective of your digital year. The stories told through data, privacy, and meaningful connection.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
