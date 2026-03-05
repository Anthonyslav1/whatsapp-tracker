package com.whatsapptracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.whatsapptracker.data.repository.YearlyReportData
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.ui.viewmodel.YearlyReportViewModel
import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun YearlyReportScreen(
    onBack: () -> Unit,
    viewModel: YearlyReportViewModel = hiltViewModel()
) {
    val reportData by viewModel.reportData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = WhatsAppGreen
                )
            }
            reportData != null -> {
                val data = reportData!!
                val totalCards = 7
                val pagerState = rememberPagerState(pageCount = { totalCards })

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(totalCards) { index ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 3.dp)
                                        .size(
                                            width = if (index == pagerState.currentPage) 20.dp else 8.dp,
                                            height = 4.dp
                                        )
                                        .clip(CircleShape)
                                        .background(
                                            if (index == pagerState.currentPage) WhatsAppGreen
                                            else TextMuted.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    // Pager with wrapped cards
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        pageSpacing = 16.dp,
                    ) { page ->
                        val isVisible = pagerState.currentPage == page
                        WrappedCard(page, data, isVisible)
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📊", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Not enough data yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Keep chatting! Your year-end report will appear here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
        }
    }
}

@Composable
private fun WrappedCard(page: Int, data: YearlyReportData, isVisible: Boolean) {
    val gradients = listOf(
        listOf(WrappedPurple1, WrappedPurple2, PrimaryPurple),
        listOf(WrappedGreen1, WrappedGreen2, WhatsAppGreen),
        listOf(WrappedPink1, WrappedPink2, PrimaryPink),
        listOf(WrappedIndigo1, WrappedIndigo2, PrimaryPurple),
        listOf(WrappedOrange1, WrappedOrange2, Color(0xFFFF9800)),
        listOf(WrappedCyan1, WrappedCyan2, Color(0xFF00BCD4)),
        listOf(WrappedPurple1, WrappedPink1, Color(0xFFFF4081)), // Entertainer gradient
    )

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(colors = gradients[page])
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (page) {
                0 -> IntroCard(data, isVisible)
                1 -> TotalTimeCard(data, isVisible)
                2 -> BestFriendCard(data, isVisible)
                3 -> TopFiveCard(data, isVisible)
                4 -> MostActiveMonthCard(data, isVisible)
                5 -> FunFactsCard(data, isVisible)
                6 -> EntertainerCard(data, isVisible)
            }

            // Screenshot hint at bottom
            Text(
                text = "Screenshot to share 📸",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun IntroCard(data: YearlyReportData, isVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "✨",
            fontSize = 64.sp,
            modifier = Modifier.graphicsLayer { this.alpha = alpha }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Your Year in\nWhatsApp",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White.copy(alpha = alpha),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            lineHeight = 52.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.year}",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White.copy(alpha = alpha * 0.6f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Swipe to explore →",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = alpha * 0.5f),
        )
    }
}

@Composable
private fun TotalTimeCard(data: YearlyReportData, isVisible: Boolean) {
    val hours = data.totalDurationMs / 3600000
    val animatedHours by animateIntAsState(
        targetValue = if (isVisible) hours.toInt() else 0,
        animationSpec = tween(1500, easing = EaseOut), label = "hours"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "You spent",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$animatedHours",
            fontSize = 96.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
        )
        Text(
            text = "hours",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "on WhatsApp this year",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Fun comparison
        val movies = (hours / 2).coerceAtLeast(1)
        Text(
            text = "That's like watching $movies movies 🎬",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BestFriendCard(data: YearlyReportData, isVisible: Boolean) {
    val bestFriend = data.topContacts.firstOrNull()
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (bestFriend != null) {
            Text(
                text = "Your #1",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.8f),
            )
            Text(
                text = "Best Friend",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = alpha * 0.8f),
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "👑",
                fontSize = 56.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = bestFriend.contactName,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            val hours = bestFriend.totalDuration / 3600000
            val minutes = (bestFriend.totalDuration % 3600000) / 60000
            Text(
                text = "${hours}h ${minutes}m together",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            if (data.totalDurationMs > 0) {
                val pct = (bestFriend.totalDuration * 100 / data.totalDurationMs).toInt()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "That's $pct% of your WhatsApp time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = alpha * 0.5f),
                )
            }
        } else {
            Text(
                text = "No chats tracked yet",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun TopFiveCard(data: YearlyReportData, isVisible: Boolean) {
    val top5 = data.topContacts.take(5)
    val maxDuration = top5.firstOrNull()?.totalDuration?.toFloat() ?: 1f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Top 5",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(32.dp))

        top5.forEachIndexed { index, contact ->
            val fraction = (contact.totalDuration / maxDuration).coerceIn(0.1f, 1f)
            val animatedFraction by animateFloatAsState(
                targetValue = if (isVisible) fraction else 0f,
                animationSpec = tween(800, delayMillis = index * 150, easing = EaseOut),
                label = "bar$index"
            )
            val hours = contact.totalDuration / 3600000
            val minutes = (contact.totalDuration % 3600000) / 60000

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. ${contact.contactName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${hours}h ${minutes}m",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (top5.isEmpty()) {
            Text(
                text = "Start chatting to see your rankings!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun MostActiveMonthCard(data: YearlyReportData, isVisible: Boolean) {
    val monthlyData = data.monthlyDurations
    val mostActive = monthlyData.maxByOrNull { it.second }
    val maxDuration = mostActive?.second?.toFloat() ?: 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Most Active Month",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White.copy(alpha = 0.8f),
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (mostActive != null) {
            Text(
                text = mostActive.first.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
            )
            val hours = mostActive.second / 3600000
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${hours} hours of chatting",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.7f),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mini month chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Month.entries.forEach { month ->
                val duration = monthlyData.find { it.first == month }?.second ?: 0L
                val fraction = (duration.toFloat() / maxDuration).coerceIn(0f, 1f)
                val barHeight = (80.dp * fraction).coerceAtLeast(2.dp)
                val isMostActive = month == mostActive?.first

                val animatedHeight by animateDpAsState(
                    targetValue = if (isVisible) barHeight else 2.dp,
                    animationSpec = tween(600, delayMillis = month.ordinal * 50),
                    label = "mBar"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(
                                if (isMostActive) Color.White
                                else Color.White.copy(alpha = 0.3f)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = month.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                        fontSize = 8.sp,
                        color = if (isMostActive) Color.White else Color.White.copy(alpha = 0.4f),
                        fontWeight = if (isMostActive) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
private fun FunFactsCard(data: YearlyReportData, isVisible: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Fun Facts",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White.copy(alpha = alpha),
            fontWeight = FontWeight.Black,
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Unique contacts
        FunFactItem(
            emoji = "👥",
            text = "You chatted with ${data.uniqueContactCount} different people",
            alpha = alpha
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Longest session
        data.longestSession?.let { session ->
            val hours = session.durationMs / 3600000
            val minutes = (session.durationMs % 3600000) / 60000
            FunFactItem(
                emoji = "⏱️",
                text = "Longest chat: ${hours}h ${minutes}m\nwith ${session.contactName}",
                alpha = alpha
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Most active day
        data.mostActiveDayOfWeek?.let { day ->
            FunFactItem(
                emoji = "📅",
                text = "You're most active on ${day.getDisplayName(TextStyle.FULL, Locale.getDefault())}s",
                alpha = alpha
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Total sessions
        FunFactItem(
            emoji = "💬",
            text = "${data.totalSessionCount} chat sessions this year",
            alpha = alpha
        )
    }
}

@Composable
private fun FunFactItem(emoji: String, text: String, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = alpha * 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = alpha * 0.9f),
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun EntertainerCard(data: YearlyReportData, isVisible: Boolean) {
    val entertainer = data.topEntertainer
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800), label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (entertainer != null) {
            Text(
                text = "The Entertainer",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White.copy(alpha = alpha * 0.9f),
                fontWeight = FontWeight.Black,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "??",
                fontSize = 64.sp,
                modifier = Modifier.graphicsLayer { this.alpha = alpha }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You watched",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            Text(
                text = entertainer.contactName,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = alpha),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "'s Status the most!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White.copy(alpha = alpha * 0.7f),
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            val hours = entertainer.totalDuration / 3600000
            val minutes = (entertainer.totalDuration % 3600000) / 60000
            val seconds = (entertainer.totalDuration % 60000) / 1000
            
            val durationText = when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m ${seconds}s"
                else -> "${seconds}s"
            }
            
            Text(
                text = "$durationText watched",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = alpha * 0.9f),
                fontWeight = FontWeight.Bold,
            )
        } else {
            Text(
                text = "No statuses watched",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}
