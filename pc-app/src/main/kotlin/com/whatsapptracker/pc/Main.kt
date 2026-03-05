package com.whatsapptracker.pc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.whatsapptracker.pc.db.Database
import com.whatsapptracker.pc.db.UsageEventRepository
import com.whatsapptracker.pc.tracker.TrackerService
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

fun main() = application {
    // Initialization
    val trackerService = remember { 
        Database.initialize()
        val service = TrackerService()
        service.startTracking()
        service
    }
    
    val repository = remember { UsageEventRepository() }

    Window(
        onCloseRequest = ::exitApplication,
        title = "WhatsApp Tracker PC"
    ) {
        MaterialTheme {
            var events by remember { mutableStateOf(repository.getAllEvents()) }
            var sessionSeconds by remember { mutableStateOf(trackerService.sessionTotalSeconds) }

            // Polling for UI updates
            LaunchedEffect(Unit) {
                while (true) {
                    sessionSeconds = trackerService.sessionTotalSeconds
                    events = repository.getAllEvents()
                    delay(1000)
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "Active Session: ${formatDuration(sessionSeconds)}",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Previous Sessions:",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(events) { event ->
                        val dateFormat = SimpleDateFormat("HH:mm:ss")
                        val startTime = dateFormat.format(Date(event.timestampStart))
                        val endTime = dateFormat.format(Date(event.timestampEnd))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Session: $startTime - $endTime")
                                Text("Duration: ${formatDuration(event.durationSeconds)}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
