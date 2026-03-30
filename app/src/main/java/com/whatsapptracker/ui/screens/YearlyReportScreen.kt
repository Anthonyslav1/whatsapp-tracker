package com.whatsapptracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whatsapptracker.R
import com.whatsapptracker.data.model.YearlyReportData
import com.whatsapptracker.ui.components.cards.*
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.ui.viewmodel.YearlyReportViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
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
                    color = CyanAccent
                )
            }
            reportData != null -> {
                val data = reportData!!
                val scrollState = rememberScrollState()
                val haptic = LocalHapticFeedback.current

                // Staggered reveals for the cards
                var revealIntro by remember { mutableStateOf(false) }
                var revealBFF by remember { mutableStateOf(false) }
                var revealPeak by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    revealIntro = true
                    delay(300)
                    revealBFF = true
                    delay(300)
                    revealPeak = true
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top sticky bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back),
                                tint = TextPrimary
                            )
                        }
                    }

                    // Feed Elements
                    AnimatedVisibility(visible = revealIntro, enter = fadeIn() + slideInVertically(initialOffsetY = { 100 })) {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            IntroCard(data, isVisible = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = revealBFF, enter = fadeIn() + slideInVertically(initialOffsetY = { 100 })) {
                        Box(modifier = Modifier.fillMaxWidth().height(420.dp).padding(horizontal = 24.dp)) {
                            BestFriendCard(data, isVisible = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedVisibility(visible = revealPeak, enter = fadeIn() + slideInVertically(initialOffsetY = { 100 })) {
                        Box(modifier = Modifier.fillMaxWidth().height(360.dp).padding(horizontal = 24.dp)) {
                            MostActiveMonthCard(data, isVisible = true)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // "Your data is yours"
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Your data is yours.",
                            style = AppTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This report is generated locally and never leaves your secure enclave. Ravdesk remains the silent guardian of your narrative.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 22.sp,
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val hours = data.totalDurationMs / 3600000
                                val bff = data.topRelationshipContacts.firstOrNull()?.contactName ?: "nobody"
                                val shareText = "I spent $hours hours on WhatsApp this year.\nMy true Best Friend by Engagement Score is $bff.\n\nCheck your own communication metadata!"
                                val intent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share your Meta"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Color.Black),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text(text = "DOWNLOAD SECURE PDF", style = MaterialTheme.typography.labelLarge, fontFamily = InterFontFamily, letterSpacing = 1.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
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
