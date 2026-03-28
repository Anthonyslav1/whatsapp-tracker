package com.whatsapptracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.whatsapptracker.service.SessionTrackerManager

class ScreenStateReceiver(
    private val sessionTracker: SessionTrackerManager
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            Log.d("ScreenStateReceiver", "Screen turned off, ending active session")
            sessionTracker.endSession()
        }
    }
}
