package com.whatsapptracker.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.ZoneId

@RunWith(RobolectricTestRunner::class)
class ChatSessionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ChatSessionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.chatSessionDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testGetMonthlyDurations() = runBlocking {
        val janStart = 1704067200000L // 2024-01-01
        val febStart = 1706745600000L // 2024-02-01
        
        // Insert sessions
        dao.insert(ChatSession(contactName = "Alice", startTime = janStart, endTime = janStart + 1000, durationMs = 1000))
        dao.insert(ChatSession(contactName = "Bob", startTime = janStart + 1000, endTime = janStart + 6000, durationMs = 5000))
        dao.insert(ChatSession(contactName = "Alice", startTime = febStart, endTime = febStart + 2000, durationMs = 2000))

        val result = dao.getMonthlyDurations(janStart, febStart + 10000)
        
        assertEquals(2, result.size)
        // 1 = Jan, 2 = Feb
        assertEquals(1, result[0].month)
        assertEquals(6000L, result[0].totalDuration)
        
        assertEquals(2, result[1].month)
        assertEquals(2000L, result[1].totalDuration)
    }
}
