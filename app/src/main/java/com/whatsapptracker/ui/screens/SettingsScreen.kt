package com.whatsapptracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.whatsapptracker.R
import com.whatsapptracker.ui.theme.*
import com.whatsapptracker.utils.isIgnoringBatteryOptimizations
import com.whatsapptracker.utils.requestIgnoreBatteryOptimizations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isIgnoringBattery by remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isIgnoringBattery = isIgnoringBatteryOptimizations(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding()
    ) {
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
                    contentDescription = stringResource(R.string.go_back),
                    tint = TextPrimary
                )
            }
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Battery Optimization
        SettingsSection(title = stringResource(R.string.settings_section_permissions)) {
            SettingsItem(
                icon = {
                    Icon(
                        Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = if (isIgnoringBattery) WhatsAppGreen else androidx.compose.ui.graphics.Color(0xFFFF9800)
                    )
                },
                title = stringResource(R.string.settings_battery_title),
                subtitle = if (isIgnoringBattery) stringResource(R.string.settings_battery_optimized) else stringResource(R.string.settings_battery_warning),
                onClick = {
                    if (!isIgnoringBattery) {
                        requestIgnoreBatteryOptimizations(context)
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsSection(title = stringResource(R.string.settings_section_about)) {
            SettingsItem(
                icon = {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                title = stringResource(R.string.app_name),
                subtitle = "Version 1.0 (Alpha)",
                onClick = {}
            )
            SettingsItem(
                icon = {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                title = stringResource(R.string.setup_privacy),
                subtitle = "No data is sent to the cloud.",
                onClick = {}
            )
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
