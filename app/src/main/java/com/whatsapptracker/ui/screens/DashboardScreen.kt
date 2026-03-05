package com.whatsapptracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.whatsapptracker.data.db.ContactDuration
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWrapped: () -> Unit,
    onNavigateToSetup: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayDuration by viewModel.todayTotalDuration.collectAsStateWithLifecycle()
    val topContacts by viewModel.todayTopContacts.collectAsStateWithLifecycle()
    val topEntertainers by viewModel.todayTopEntertainers.collectAsStateWithLifecycle()
    val weeklyTotals by viewModel.weeklyTotals.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WhatsApp Tracker",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Today's activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            IconButton(onClick = onNavigateToSetup) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary,
                )
            }
        }

        // Today's total time card
        TodayTimeCard(todayDuration)

        Spacer(modifier = Modifier.height(20.dp))

        // Wrapped CTA
        WrappedBanner(onClick = onNavigateToWrapped)

        Spacer(modifier = Modifier.height(24.dp))

        // Top contacts
        SectionHeader("Top Contacts Today")
        if (topContacts.isEmpty()) {
            EmptyState("Start chatting on WhatsApp to see your top contacts here")
        } else {
            TopContactsList(topContacts)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Top Entertainers
        SectionHeader("Top Entertainers Today 🍿")
        if (topEntertainers.isEmpty()) {
            EmptyState("View WhatsApp statuses to see whose statuses you watch the most")
        } else {
            TopContactsList(topEntertainers)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly chart
        SectionHeader("Last 7 Days")
        WeeklyChart(weeklyTotals)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TodayTimeCard(durationMs: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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
                .padding(28.dp),
        ) {
            Column {
                Text(
                    text = "Screen Time",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDuration(durationMs),
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "on WhatsApp today",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun WrappedBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(WrappedPurple2, WrappedPink2)
                    )
                )
                .padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your Year Wrapped",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Find your #1 best friend →",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun EmptyState(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun TopContactsList(contacts: List<ContactDuration>) {
    val maxDuration = contacts.maxOfOrNull { it.totalDuration }?.toFloat() ?: 1f

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        contacts.forEachIndexed { index, contact ->
            ContactRow(index + 1, contact, maxDuration)
            if (index < contacts.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
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

@Composable
private fun WeeklyChart(dailyTotals: Map<LocalDate, Long>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val maxDuration = dailyTotals.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val chartHeight = 120.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEach { date ->
                val duration = dailyTotals[date] ?: 0L
                val fraction = (duration.toFloat() / maxDuration).coerceIn(0f, 1f)
                val barHeight = (chartHeight * fraction).coerceAtLeast(4.dp)
                val isToday = date == today

                val animatedHeight by animateDpAsState(
                    targetValue = barHeight,
                    animationSpec = tween(600, easing = EaseOut),
                    label = "barH"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Duration label on top
                    if (duration > 0) {
                        Text(
                            text = formatDurationShort(duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 9.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (isToday) Brush.verticalGradient(
                                    listOf(WhatsAppGreen, WhatsAppTeal)
                                )
                                else Brush.verticalGradient(
                                    listOf(
                                        WhatsAppGreen.copy(alpha = 0.5f),
                                        WhatsAppTeal.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ).take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) WhatsAppGreen else TextMuted,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalMinutes = durationMs / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        durationMs > 0 -> "<1m"
        else -> "0m"
    }
}

fun formatDurationShort(durationMs: Long): String {
    val totalMinutes = durationMs / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 -> "${hours}h"
        minutes > 0 -> "${minutes}m"
        else -> ""
    }
}

fun formatDurationHours(durationMs: Long): String {
    val hours = durationMs / 3600000
    return if (hours > 0) "$hours" else "<1"
}
