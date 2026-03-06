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
                        text = stringResource(R.string.not_enough_data),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.keep_chatting),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(onClick = onBack) {
                        Text(stringResource(R.string.go_back))
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
                text = stringResource(R.string.screenshot_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

// Components extracted to com.whatsapptracker.ui.components.cards
