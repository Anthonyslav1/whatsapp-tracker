package com.whatsapptracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.whatsapptracker.R
import com.whatsapptracker.ui.components.*
import com.whatsapptracker.ui.components.cards.SmartInsightsCard
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.ui.viewmodel.DashboardViewModel
import com.whatsapptracker.utils.isAccessibilityServiceEnabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWrapped: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isServiceEnabled by remember { mutableStateOf(context.isAccessibilityServiceEnabled()) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isServiceEnabled = context.isAccessibilityServiceEnabled()
    }

    val todayDuration by viewModel.todayTotalDuration.collectAsStateWithLifecycle()
    val topContacts by viewModel.todayTopContacts.collectAsStateWithLifecycle()
    val topEntertainers by viewModel.todayTopEntertainers.collectAsStateWithLifecycle()
    val smartInsights by viewModel.smartInsights.collectAsStateWithLifecycle()
    val weeklyTotals by viewModel.weeklyTotals.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Header — rebranded to Ravdesk
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = TextSecondary,
                )
            }
        }

        if (!isServiceEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                onClick = onNavigateToSetup
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.service_dead_warning),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Today's total time card — more padding for breathing room
        TodayTimeCard(todayDuration)

        Spacer(modifier = Modifier.height(28.dp))

        // Wrapped CTA
        WrappedBanner(onClick = onNavigateToWrapped)

        Spacer(modifier = Modifier.height(40.dp))

        // Top contacts
        SectionHeader(stringResource(R.string.top_contacts_today))
        Spacer(modifier = Modifier.height(4.dp))
        if (topContacts.isEmpty()) {
            EmptyState(stringResource(R.string.top_contacts_empty))
        } else {
            TopContactsList(topContacts)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Top Entertainers — visually differentiated with purple accent + horizontal pills
        SectionHeader(stringResource(R.string.top_entertainers_today), emoji = "🍿")
        Spacer(modifier = Modifier.height(4.dp))
        if (topEntertainers.isEmpty()) {
            EmptyState(stringResource(R.string.top_entertainers_empty))
        } else {
            EntertainersPillRow(topEntertainers)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Weekly chart
        SectionHeader(stringResource(R.string.last_7_days))
        Spacer(modifier = Modifier.height(4.dp))
        WeeklyChart(weeklyTotals)

        Spacer(modifier = Modifier.height(24.dp))
        SmartInsightsCard(insightText = smartInsights)

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SectionHeader(title: String, emoji: String? = null) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        if (emoji != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = emoji, fontSize = 18.sp)
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
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
