package com.whatsapptracker.pc

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.whatsapptracker.pc.db.Database
import com.whatsapptracker.pc.db.TopContact
import com.whatsapptracker.pc.db.UsageEvent
import com.whatsapptracker.pc.db.UsageEventRepository
import com.whatsapptracker.pc.tracker.TrackerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

// ── Color Palette (mirrors Android Ravdesk) ──────────────────────────
private val DarkBackground = Color(0xFF0A0A0F)
private val DarkSurface = Color(0xFF141420)
private val DarkSurfaceVariant = Color(0xFF1E1E2E)
private val WhatsAppGreen = Color(0xFF25D366)
private val WhatsAppDarkGreen = Color(0xFF128C7E)
private val WhatsAppTeal = Color(0xFF075E54)
private val TextPrimary = Color(0xFFF5F5F5)
private val TextSecondary = Color(0xFFB0B0C0)
private val TextMuted = Color(0xFF6E6E80)
private val AccentPurple = Color(0xFF7C4DFF)

private val RavdeskDarkColors = darkColorScheme(
    primary = WhatsAppGreen,
    onPrimary = DarkBackground,
    secondary = AccentPurple,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
)

fun main() = application {
    // Shared repository — single instance used by both service and UI
    val repository = remember { UsageEventRepository() }

    val trackerService = remember {
        Database.initialize()
        val service = TrackerService(repository)
        service.startTracking()
        service
    }

    Window(
        onCloseRequest = {
            // Graceful shutdown: flush active session + close DB
            trackerService.stop()
            Database.close()
            exitApplication()
        },
        title = "Ravdesk",
        state = WindowState(size = DpSize(440.dp, 800.dp)),
    ) {
        MaterialTheme(colorScheme = RavdeskDarkColors) {
            // Live data (polled every 1s — cheap, no DB)
            var liveSeconds by remember { mutableStateOf(0L) }
            var isActive by remember { mutableStateOf(false) }
            var activeChat by remember { mutableStateOf<String?>(null) } // To show live in the best friend/hero

            // DB data (polled every 5s on IO thread)
            var todaySessions by remember { mutableStateOf(emptyList<UsageEvent>()) }
            var todayTotalSeconds by remember { mutableStateOf(0L) }
            var weeklyTotals by remember { mutableStateOf(emptyMap<LocalDate, Long>()) }
            var sessionCount by remember { mutableStateOf(0) }
            var topContacts by remember { mutableStateOf(emptyList<TopContact>()) }

            // Fast poll: live counter (1s, no DB hit)
            LaunchedEffect(Unit) {
                while (true) {
                    liveSeconds = trackerService.getLiveSessionSeconds()
                    isActive = trackerService.isCurrentlyTracking
                    activeChat = trackerService.currentChatName
                    delay(1000)
                }
            }

            // Slow poll: DB queries (5s, on IO dispatcher)
            LaunchedEffect(Unit) {
                while (true) {
                    withContext(Dispatchers.IO) {
                        todaySessions = repository.getTodaySessions()
                        todayTotalSeconds = repository.getTodayTotalSeconds()
                        weeklyTotals = repository.getWeeklyTotals()
                        sessionCount = repository.getTodaySessionCount()
                        topContacts = repository.getTopContactsAllTime(10)
                    }
                    delay(5000)
                }
            }

            RavdeskDashboard(
                todayTotalSeconds = todayTotalSeconds,
                liveSessionSeconds = liveSeconds,
                isLiveActive = isActive,
                activeChat = activeChat,
                sessionCount = sessionCount,
                todaySessions = todaySessions,
                weeklyTotals = weeklyTotals,
                topContacts = topContacts,
            )
        }
    }
}

@Composable
fun RavdeskDashboard(
    todayTotalSeconds: Long,
    liveSessionSeconds: Long,
    isLiveActive: Boolean,
    activeChat: String?,
    sessionCount: Int,
    todaySessions: List<UsageEvent>,
    weeklyTotals: Map<LocalDate, Long>,
    topContacts: List<TopContact>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Header ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Ravdesk",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "WhatsApp Desktop Tracker",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ── Hero: Today's Screen Time ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
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
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                    ) {
                        Column {
                            Text(
                                text = "Screen Time",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val totalDisplaySeconds = todayTotalSeconds + if (isLiveActive) liveSessionSeconds else 0
                            Text(
                                text = formatDuration(totalDisplaySeconds),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ── Live Session Indicator ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                LiveSessionCard(isLiveActive, activeChat, liveSessionSeconds)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ── Top Entertainers (Horizontal Pills) ──
        if (topContacts.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Top Entertainers",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        topContacts.forEachIndexed { index, contact ->
                            // Calculate live duration additive if they are the active chat
                            val addLive = if (isLiveActive && contact.chatName == activeChat) liveSessionSeconds else 0L
                            EntertainerPill(
                                rank = index + 1,
                                name = contact.chatName ?: "Unknown",
                                durationSeconds = contact.totalSeconds + addLive
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            
            // ── Best Friend Card ──
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    val bestFriend = topContacts.first()
                    val addLive = if (isLiveActive && bestFriend.chatName == activeChat) liveSessionSeconds else 0L
                    BestFriendCard(
                        name = bestFriend.chatName ?: "Unknown",
                        durationSeconds = bestFriend.totalSeconds + addLive
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // ── Stats Row ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Sessions",
                        value = "$sessionCount",
                        emoji = "💬"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Avg Length",
                        value = if (sessionCount > 0) formatDuration(todayTotalSeconds / sessionCount) else "—",
                        emoji = "⏱️"
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ── Weekly Chart ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionLabel("Last 7 Days")
                Spacer(modifier = Modifier.height(16.dp))
                WeeklyBarChart(weeklyTotals)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ── Today's Sessions ──
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                SectionLabel("Today's Breakdown")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (todaySessions.isEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    ) {
                        Text(
                            text = "No sessions recorded today. Open WhatsApp to start tracking.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }
        } else {
            items(todaySessions) { event ->
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SessionRow(event, isLiveActive, activeChat, liveSessionSeconds)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ── Components ──────────────────────────────────────────────────────

@Composable
fun LiveSessionCard(isActive: Boolean, activeChat: String?, seconds: Long) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) WhatsAppGreen.copy(alpha = pulseAlpha)
                        else TextMuted.copy(alpha = 0.3f)
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActive) (activeChat ?: "Menu") else "No active session",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isActive) TextPrimary else TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isActive) {
                    Text(
                        text = formatDuration(seconds),
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhatsAppGreen,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (isActive) {
                Text(
                    text = "LIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = WhatsAppGreen.copy(alpha = pulseAlpha),
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
fun EntertainerPill(rank: Int, name: String, durationSeconds: Long) {
    val isFirst = rank == 1
    val backgroundBrush = if (isFirst) {
        Brush.horizontalGradient(listOf(Color(0xFF9C27B0), AccentPurple))
    } else {
        Brush.horizontalGradient(listOf(DarkSurfaceVariant, DarkSurfaceVariant))
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundBrush)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🍿",
                    fontSize = 20.sp
                )
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFirst) Color.White.copy(alpha = 0.6f) else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isFirst) Color.White else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatDuration(durationSeconds),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isFirst) Color.White.copy(alpha = 0.8f) else WhatsAppGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BestFriendCard(name: String, durationSeconds: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhatsAppDarkGreen.copy(alpha = 0.15f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("💖", fontSize = 28.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Best Friend",
                    style = MaterialTheme.typography.labelMedium,
                    color = WhatsAppGreen,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatDuration(durationSeconds)} total spent chatting",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, label: String, value: String, emoji: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun WeeklyBarChart(weeklyTotals: Map<LocalDate, Long>) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val maxSeconds = weeklyTotals.values.maxOrNull()?.coerceAtLeast(1L) ?: 1L
    val chartHeight = 100.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEach { date ->
                val seconds = weeklyTotals[date] ?: 0L
                val fraction = (seconds.toFloat() / maxSeconds).coerceIn(0f, 1f)
                val barHeight = (chartHeight * fraction).coerceAtLeast(4.dp)
                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    if (seconds > 0) {
                        Text(
                            text = formatDurationShort(seconds),
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
                            .width(20.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
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
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) WhatsAppGreen else TextMuted,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
fun SessionRow(event: UsageEvent, isLiveActive: Boolean, activeChat: String?, liveSessionSeconds: Long) {
    val isCurrentActive = isLiveActive && activeChat == event.chatName
    val totalSeconds = event.durationSeconds + if (isCurrentActive) liveSessionSeconds else 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.chatName ?: "Menu/Legacy",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(WhatsAppGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = formatDuration(totalSeconds),
                    style = MaterialTheme.typography.labelLarge,
                    color = WhatsAppGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ── Utilities ────────────────────────────────────────────────────────

fun formatDuration(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        hrs > 0 -> "${hrs}h ${mins}m"
        mins > 0 -> "${mins}m ${secs}s"
        else -> "${secs}s"
    }
}

fun formatDurationShort(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    return when {
        hrs > 0 -> "${hrs}h"
        mins > 0 -> "${mins}m"
        else -> ""
    }
}
