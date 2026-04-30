package com.whatsapptracker.ui.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.ui.screens.formatDuration
import com.whatsapptracker.ui.theme.CyanAccent
import com.whatsapptracker.ui.theme.DarkSurfaceVariant
import com.whatsapptracker.ui.theme.TextPrimary
import com.whatsapptracker.ui.theme.TextSecondary

@Composable
fun TopContactsList(contacts: List<ContactDuration>) {
    val maxDuration = contacts.maxOfOrNull { it.totalDuration }?.toFloat() ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            contacts.take(5).forEachIndexed { index, contact ->
                ContactRow(index + 1, contact, maxDuration)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clearAndSetSemantics {
                contentDescription = "Rank $rank: ${contact.contactName}, time spent: ${formatDuration(contact.totalDuration)}"
            }
    ) {
        // Subtle Avatar Placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2D35)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.contactName.take(1).uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            // Tiny rank badge overlaid on bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (rank == 1) CyanAccent else Color(0xFF6E6E80)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.contactName,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Fake role or duration
            Text(
                text = formatDuration(contact.totalDuration),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        // Lightning bolt icon indicating activity
        Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = CyanAccent.copy(alpha = 0.6f + (0.4f * animatedFraction)),
            modifier = Modifier.size(20.dp)
        )
    }
}
