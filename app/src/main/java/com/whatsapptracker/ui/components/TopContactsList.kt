package com.whatsapptracker.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.ui.screens.formatDuration
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextPrimary
import com.whatsapptracker.ui.theme.TextSecondary
import com.whatsapptracker.ui.theme.WhatsAppDarkGreen
import com.whatsapptracker.ui.theme.WhatsAppGreen

@Composable
fun TopContactsList(contacts: List<ContactDuration>) {
    val maxDuration = contacts.maxOfOrNull { it.totalDuration }?.toFloat() ?: 1f

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        contacts.forEachIndexed { index, contact ->
            ContactRow(index + 1, contact, maxDuration)
            if (index < contacts.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(rank: Int, contact: ContactDuration, maxDuration: Float) {
    val fraction = (contact.totalDuration / maxDuration).coerceIn(0.05f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(800, delayMillis = rank * 100, easing = EaseOut),
        label = "bar"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Rank badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (rank == 1) WhatsAppGreen else DarkSurfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                style = MaterialTheme.typography.labelSmall,
                color = if (rank == 1) Color.White else TextSecondary,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = contact.contactName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = formatDuration(contact.totalDuration),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WhatsAppGreen,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(WhatsAppGreen, WhatsAppDarkGreen)
                        )
                    )
            )
        }
    }
}
