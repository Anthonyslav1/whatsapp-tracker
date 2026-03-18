package com.whatsapptracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatSessionDao(): ChatSessionDao
}
