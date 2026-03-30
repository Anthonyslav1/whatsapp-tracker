package com.whatsapptracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whatsapptracker.R
import com.whatsapptracker.ui.components.*
import com.whatsapptracker.ui.components.cards.SmartInsightsCard
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.ui.viewmodel.DashboardViewModel
import com.whatsapptracker.utils.isAccessibilityServiceEnabled
import kotlinx.coroutines.delay

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

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val todayDuration by viewModel.todayTotalDuration.collectAsStateWithLifecycle()
    val topContacts by viewModel.todayTopContacts.collectAsStateWithLifecycle()
    val topEntertainers by viewModel.todayTopEntertainers.collectAsStateWithLifecycle()
    val smartInsights by viewModel.smartInsights.collectAsStateWithLifecycle()
    val weeklyTotals by viewModel.weeklyTotals.collectAsStateWithLifecycle()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Main Cinematic Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "System\nIntegrity",
                    style = AppTypography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    lineHeight = 40.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Deep monitoring of encrypted\ncommunication streams and vault\ninteraction peaks.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                )
            }
            IconButton(onClick = onNavigateToSettings, modifier = Modifier.align(Alignment.Top)) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = TextSecondary,
                )
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { -50 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        ) {
            DateSelectorStrip(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    isVisible = false
                    viewModel.selectDate(date)
                }
            )
        }
        
        LaunchedEffect(selectedDate) {
            if (!isVisible) {
                delay(50)
                isVisible = true
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        // System Integrity Card (Replaces TodayTimeCard)
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        ) {
            TodayTimeCard(todayDuration)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Wrapped CTA
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 100)) + fadeIn(animationSpec = tween(500, delayMillis = 100))
        ) {
            WrappedBanner(onClick = onNavigateToWrapped)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Top contacts component
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 200)) + fadeIn(animationSpec = tween(500, delayMillis = 200))
        ) {
            Column {
                SectionHeader("Top Contacts")
                Spacer(modifier = Modifier.height(4.dp))
                if (topContacts.isEmpty()) {
                    EmptyState(stringResource(R.string.top_contacts_empty))
                } else {
                    TopContactsList(topContacts)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Weekly chart component
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 300)) + fadeIn(animationSpec = tween(500, delayMillis = 300))
        ) {
            Column {
                SectionHeader("Weekly Trend", subtitle = "Usage peaks across all encrypted endpoints")
                Spacer(modifier = Modifier.height(12.dp))
                WeeklyChart(weeklyTotals)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Top Entertainers
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500, delayMillis = 400)) + fadeIn(animationSpec = tween(500, delayMillis = 400))
        ) {
            Column {
                SectionHeader(stringResource(R.string.top_entertainers_today), emoji = "🍿")
                Spacer(modifier = Modifier.height(4.dp))
                if (topEntertainers.isEmpty()) {
                    EmptyState(stringResource(R.string.top_entertainers_empty))
                } else {
                    EntertainersPillRow(topEntertainers)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        SmartInsightsCard(insightText = smartInsights)

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String? = null, emoji: String? = null) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = AppTypography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Normal,
            )
            if (emoji != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = emoji, fontSize = 20.sp)
            }
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
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
