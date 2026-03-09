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

    /**
     * Extracts the specific chat name from the window title if available.
     * Returns null if WhatsApp is not foregrounded or no specific chat is open.
     */
    fun getActiveChatName(): String? {
        if (!isWhatsAppForeground()) return null

        var title = getActiveWindowTitle()
        
        // WhatsApp window titles format:
        // "Alice - WhatsApp"
        // "(2) Bob - WhatsApp"
        // "WhatsApp" (main menu)
        
        if (title == "WhatsApp") return null

        // Remove the standard suffix
        title = title.removeSuffix(" - WhatsApp").trim()

        // Strip notification badge like "(2) "
        val badgeRegex = Regex("^\\(\\d+\\)\\s*")
        title = title.replace(badgeRegex, "").trim()

        return if (title.isEmpty()) null else title
    }
}
