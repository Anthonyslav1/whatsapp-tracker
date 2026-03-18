package com.whatsapptracker

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.rememberNavController
import com.whatsapptracker.navigation.AppNavGraph
import com.whatsapptracker.ui.theme.WhatsAppTrackerTheme
import com.whatsapptracker.utils.isAccessibilityServiceEnabled
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WhatsAppTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    var isAccessibilityEnabled by remember { mutableStateOf(context.isAccessibilityServiceEnabled()) }

    // Re-check when app resumes (user might have just enabled it in Settings)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isAccessibilityEnabled = context.isAccessibilityServiceEnabled()
    }

    AppNavGraph(
        navController = navController,
        isAccessibilityEnabled = isAccessibilityEnabled
    )
}
