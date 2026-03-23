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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whatsapptracker.data.model.YearlyReportData

@Composable
fun RelationshipScoreCard(data: YearlyReportData, isVisible: Boolean) {
    val bestRelationship = data.topRelationshipContacts.firstOrNull()
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (bestRelationship != null) {
            Text(
                text = "YOUR REAL",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.8f),
            )
            Text(
                text = "BEST FRIEND",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = alpha * 0.8f),
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "💖",
                fontSize = 56.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = bestRelationship.contactName,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Highest Engagement Score",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Based on frequent burst communication.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = alpha * 0.5f),
                textAlign = TextAlign.Center
            )
            
        } else {
            Text(
                text = "No chats found",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}
