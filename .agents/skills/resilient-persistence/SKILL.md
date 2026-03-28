---
name: resilient-persistence
description: >
  Use this skill when building or modifying anything related to writing session
  data reliably to the Room database, handling app crashes or OS-kill scenarios,
  managing WorkManager background jobs, the SessionManager state machine, or
  the open-session dead-man's-switch logic. Triggers for any work involving
  SessionManager, SaveSessionWorker, HiltWorker, AppDatabase, DatabaseModule,
  session open/close lifecycle, crash-recovery logic, or WorkManager enqueueing.
  If the user mentions "session not saved on crash", "WorkManager task", "data
  loss", "background service killed", "Room insert", or "open session recovery",
  always use this skill.
---

# Resilient Persistence Skill — The Background Sentinel

## 1. Overview

**Mission:** Guarantee that every viewing session — regardless of whether the
app crashes, the OS kills the service, or the user force-stops the app — is
eventually written to the Room database with an accurate `end_timestamp` and
`duration_ms`. Zero data loss, zero corrupted rows.

**Core principle:** Never trust the happy path. Assume the process will die at
any moment. Write defensively.

---

## 2. When to Use

- Implementing or debugging `SessionManager.kt` (the open/close state machine)
- Implementing or debugging `SaveSessionWorker.kt` (the WorkManager dead-man's-switch)
- Setting up `AppDatabase` and `DatabaseModule` (Hilt singleton provisioning)
- Writing or modifying `SessionDao.kt` (insert, update, crash recovery queries)
- Handling the boot / service-restart scenario where an open session was never closed
- Tuning WorkManager constraints (battery, network, retry policy)

## 3. When NOT to Use

- Parsing WhatsApp UI to produce session events → use `contextual-ingestion` skill
- Calculating engagement scores from stored rows → use `engagement-scoring` skill
- Compiling Wrapped reports for sharing → use `viral-payload-generator` skill

---

## 4. Architecture: The Two-Path Guarantee

```
RawSessionEvent (from AccessibilityService)
    │
    ▼
SessionManager.kt                       ← In-memory state machine
    │
    ├── HAPPY PATH: user navigates away  → closeSession() → DAO.closeSession()
    │
    └── CRASH PATH: process killed       → WorkManager fires SaveSessionWorker
                                            on next device boot/charge
                                            → DAO.forceCloseOrphanedSessions()
```

Both paths write the same `sessions` table. The result is identical.

---

## 5. Room Database Setup

### 5.1 Entity

```kotlin
// data/local/entity/SessionEntity.kt
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "contact_name")
    val contactName: String,

    @ColumnInfo(name = "session_type")
    val sessionType: String,          // "CHAT" | "STATUS"

    @ColumnInfo(name = "start_timestamp")
    val startTimestamp: Long,         // epoch millis

    @ColumnInfo(name = "end_timestamp")
    val endTimestamp: Long? = null,   // null = still open

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long? = null,     // null until session closed
)
```

### 5.2 DAO

```kotlin
// data/local/dao/SessionDao.kt
@Dao
interface SessionDao {

    // Opens a new session row (end_timestamp intentionally null)
    @Insert
    suspend fun openSession(session: SessionEntity): Long   // returns row id

    // Closes a specific session by id
    @Query("""
        UPDATE sessions
        SET end_timestamp = :endTs,
            duration_ms   = :endTs - start_timestamp
        WHERE id = :sessionId
          AND end_timestamp IS NULL
    """)
    suspend fun closeSession(sessionId: Long, endTs: Long)

    // Dead-man's-switch: close ALL orphaned open sessions using NOW as end time.
    // Called by SaveSessionWorker after a crash/kill.
    @Query("""
        UPDATE sessions
        SET end_timestamp = :nowTs,
            duration_ms   = :nowTs - start_timestamp
        WHERE end_timestamp IS NULL
          AND duration_ms  IS NULL
          AND start_timestamp < :cutoffTs   -- ignore sessions opened in last 5s (race guard)
    """)
    suspend fun forceCloseOrphanedSessions(nowTs: Long, cutoffTs: Long = nowTs - 5_000)

    // Cleanup: delete sessions with nonsensical data (negative duration, <1 second)
    @Query("DELETE FROM sessions WHERE duration_ms IS NOT NULL AND duration_ms < 1000")
    suspend fun deleteGarbageSessions()
}
```

### 5.3 Database Class

```kotlin
// data/local/AppDatabase.kt
@Database(entities = [SessionEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}
```

### 5.4 Hilt Module (Singleton — No Manual Singleton in the Class)

```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ravdesk.db")
            .fallbackToDestructiveMigration()   // swap for real migrations in prod
            .build()

    @Provides
    @Singleton
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}
```

---

## 6. SessionManager — The State Machine

```kotlin
// service/SessionManager.kt
@Singleton
class SessionManager @Inject constructor(
    private val sessionDao: SessionDao,
    private val workManager: WorkManager,
) {
    private var openSessionId: Long? = null
    private var openSessionStartTs: Long = 0L

    // Called when parser detects an active WhatsApp screen
    suspend fun onScreenActive(event: RawSessionEvent) {
        // If same contact/type is already open, do nothing (debounce)
        if (openSessionId != null) return

        val entity = SessionEntity(
            contactName    = event.contactName,
            sessionType    = event.sessionType.name,
            startTimestamp = event.startTimestamp,
        )
        openSessionId = sessionDao.openSession(entity)
        openSessionStartTs = event.startTimestamp

        // Enqueue the dead-man's-switch worker immediately
        enqueueDeadManSwitch()
    }

    // Called when parser returns Inactive (user left WhatsApp or switched screen)
    suspend fun onScreenInactive() {
        val id = openSessionId ?: return
        sessionDao.closeSession(id, System.currentTimeMillis())
        openSessionId = null

        // Cancel the dead-man's-switch — we closed cleanly
        workManager.cancelUniqueWork(DEAD_MAN_SWITCH_TAG)
    }

    private fun enqueueDeadManSwitch() {
        val request = OneTimeWorkRequestBuilder<SaveSessionWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .setInitialDelay(0, TimeUnit.SECONDS)
            // Back-off: if worker fails, retry with exponential backoff
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            DEAD_MAN_SWITCH_TAG,
            ExistingWorkPolicy.REPLACE,   // replace if already queued
            request
        )
    }

    companion object {
        const val DEAD_MAN_SWITCH_TAG = "save_session_worker"
    }
}
```

---

## 7. SaveSessionWorker — The Dead-Man's Switch

```kotlin
// worker/SaveSessionWorker.kt
@HiltWorker
class SaveSessionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sessionDao: SessionDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            sessionDao.forceCloseOrphanedSessions(nowTs = now)
            sessionDao.deleteGarbageSessions()
            Result.success()
        } catch (e: Exception) {
            // Retry — DB may be locked by another operation
            Result.retry()
        }
    }
}
```

---

## 8. WorkManager — On-Demand Initialization

To avoid WorkManager initializing before Hilt is ready (a common crash source),
exclude the default initializer and provide your own.

```xml
<!-- AndroidManifest.xml -->
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="androidx.work.WorkManagerInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

```kotlin
// RavdeskApplication.kt
@HiltAndroidApp
class RavdeskApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
```

---

## 9. Boot Recovery (Closing Sessions from a Reboot)

If the device reboots mid-session, no Accessibility event fires to close it.
Register a boot receiver to fire `forceCloseOrphanedSessions` on startup.

```kotlin
// receiver/BootReceiver.kt
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var sessionDao: SessionDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // Run in a coroutine via goAsync()
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sessionDao.forceCloseOrphanedSessions(System.currentTimeMillis())
                sessionDao.deleteGarbageSessions()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
```

```xml
<!-- AndroidManifest.xml -->
<receiver android:name=".receiver.BootReceiver" android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
    </intent-filter>
</receiver>
```

---

## 10. Testing

```kotlin
@Test
fun `forceCloseOrphanedSessions closes open rows and sets duration`() = runTest {
    val startTs = System.currentTimeMillis() - 60_000  // 1 minute ago
    val id = dao.openSession(SessionEntity("Alice", "CHAT", startTs))
    // Simulate crash — never called closeSession()

    val now = System.currentTimeMillis()
    dao.forceCloseOrphanedSessions(nowTs = now)

    val session = dao.getSessionById(id)
    assertThat(session.endTimestamp).isNotNull()
    assertThat(session.durationMs).isAtLeast(59_000L)  // ~60 seconds
}

@Test
fun `garbage sessions under 1 second are deleted`() = runTest {
    dao.openSession(SessionEntity("Bob", "CHAT", System.currentTimeMillis() - 500))
    dao.forceCloseOrphanedSessions(System.currentTimeMillis())
    dao.deleteGarbageSessions()

    val all = dao.getAllSessions()
    assertThat(all).isEmpty()
}
```

---

## 11. Gotchas

- **Never call Room on the main thread.** All DAO operations must be `suspend` and called from `Dispatchers.IO` or within a `CoroutineWorker`. Room will throw if called on main in strict mode.
- **`ExistingWorkPolicy.REPLACE` is critical.** Without it, if a new session opens before the previous worker ran, you end up with two orphan-closing workers racing each other.
- **`fallbackToDestructiveMigration()` deletes all data on schema change.** Replace with proper migration scripts before shipping to production users.
- **`cutoffTs` race guard in `forceCloseOrphanedSessions`.** The 5-second exclusion window prevents the worker from closing a session that was *just* opened in the same process startup sequence.
- **HiltWorker requires `@AssistedInject`.** Forgetting `@AssistedInject` on the constructor causes a runtime crash at worker instantiation, not a compile error.
