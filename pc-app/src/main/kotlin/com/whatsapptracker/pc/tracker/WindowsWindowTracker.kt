package com.whatsapptracker.pc.tracker

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

// Interface for User32.dll to get foreground window and title
interface User32 : StdCallLibrary {
    fun GetForegroundWindow(): WinDef.HWND?
    fun GetWindowTextW(hWnd: WinDef.HWND, lpString: CharArray, nMaxCount: Int): Int
    fun GetWindowThreadProcessId(hWnd: WinDef.HWND, lpdwProcessId: IntByReference): Int

    companion object {
        val INSTANCE: User32 = Native.load(
            "user32",
            User32::class.java,
            W32APIOptions.DEFAULT_OPTIONS
        ) as User32
    }
}

class WindowsWindowTracker {

    fun getActiveWindowTitle(): String {
        return try {
            val hwnd = User32.INSTANCE.GetForegroundWindow() ?: return ""
            val buffer = CharArray(1024)
            User32.INSTANCE.GetWindowTextW(hwnd, buffer, 1024)
            String(buffer).trim { it <= ' ' || it == '\u0000' }
        } catch (e: Exception) {
            // JNA failure (DLL not found, security policy, etc.)
            ""
        }
    }

    fun isWhatsAppForeground(): Boolean {
        val title = getActiveWindowTitle()
        return title.contains("WhatsApp", ignoreCase = true)
    }
}
