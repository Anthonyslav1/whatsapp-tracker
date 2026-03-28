---
name: engagement-scoring
description: >
  Use this skill when building or modifying the Relationship Score algorithm,
  Smart Insights categorization, or any analytical computation that transforms
  raw session rows into ranked contacts or behavioral profiles. Triggers for
  work on UsageRepository, DAO query design, Kotlin Coroutine-based aggregation,
  relationship weighting formulas, "Best Friend" calculation, "The Entertainer"
  (status-view ranking), "burst vs balanced" communicator detection, or any
  feature that presents ranked/scored contact data to the user. If the user
  mentions "engagement metric", "who do I talk to most", "relationship score",
  "smart insights", "communication style", or "contact ranking", always use
  this skill.
---

# Engagement Scoring Skill — The Brain of Ravdesk

## 1. Overview

**Mission:** Transform a flat log of timestamped session rows into meaningful,
ranked, and categorized insights about a user's communication behaviour. The
outputs power the Dashboard, the Yearly Wrapped report, and all "Best of"
statistics.

**Core principle:** Time alone is not engagement. A user who opens a chat 50
times for 10 seconds each has a different relationship than one who spends
20 minutes in a single call. The scoring model explicitly captures both
dimensions.

---

## 2. When to Use

- Implementing or tuning the `RelationshipScore` formula
- Writing or optimizing Room DAO queries that aggregate session data
- Implementing "Smart Insights" (burst vs. balanced communicator detection)
- Building "The Entertainer" (top status-view contacts)
- Producing inputs for the Yearly Wrapped report (Top 5, Most Active Month,
  Fun Facts)
- Debugging incorrect contact rankings or counter-intuitive scores

## 3. When NOT to Use

- Raw event capture from WhatsApp UI → use `contextual-ingestion` skill
- Saving/persisting sessions to Room → use `resilient-persistence` skill
- Formatting or sharing the Wrapped output → use `viral-payload-generator` skill

---

## 4. Architecture

```
Room Database (AppDatabase)
    │
    ▼
SessionDao.kt                ← Raw SQL aggregations via @Query
    │
    ▼
UsageRepositoryImpl.kt       ← Business logic layer
    │  combineAggregates()
    │  computeRelationshipScore()
    │  classifyCommunicationStyle()
    ▼
DashboardUiState / YearlyReportData
    │
    ▼
DashboardViewModel / YearlyReportViewModel
```

---

## 5. Database Schema Reference

```sql
CREATE TABLE sessions (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    contact_name    TEXT    NOT NULL,
    session_type    TEXT    NOT NULL,       -- 'CHAT' | 'STATUS'
    start_timestamp INTEGER NOT NULL,       -- epoch millis
    end_timestamp   INTEGER,               -- null if session is still open
    duration_ms     INTEGER                -- computed on close: end - start
);
```

---

## 6. The Relationship Score Formula

### 6.1 Definition

```
RelationshipScore(contact) =
    (total_duration_ms / 1000) + (session_count × FREQUENCY_WEIGHT)

where FREQUENCY_WEIGHT = 30   // 30 "score points" per additional interaction
```

**Why this formula?**
- A single 10-minute session = 600 score points
- 20 × 30-second sessions = 600 + (20 × 30) = 1200 score points

This deliberately rewards consistent, repeated interaction over a single long
session — matching the intuition that frequent check-ins define a closer
relationship than one long accidental call.

### 6.2 DAO Query

```kotlin
// SessionDao.kt
@Query("""
    SELECT
        contact_name,
        SUM(duration_ms)                        AS total_duration_ms,
        COUNT(*)                                AS session_count,
        (SUM(duration_ms) / 1000.0)
            + (COUNT(*) * :frequencyWeight)     AS relationship_score
    FROM sessions
    WHERE session_type = 'CHAT'
      AND end_timestamp IS NOT NULL
      AND start_timestamp BETWEEN :fromEpoch AND :toEpoch
    GROUP BY contact_name
    ORDER BY relationship_score DESC
    LIMIT :limit
""")
suspend fun getTopContactsByScore(
    fromEpoch: Long,
    toEpoch: Long,
    frequencyWeight: Float = 30f,
    limit: Int = 10
): List<ContactScoreDto>
```

### 6.3 DTO

```kotlin
// Models.kt
data class ContactScoreDto(
    val contactName: String,
    val totalDurationMs: Long,
    val sessionCount: Int,
    val relationshipScore: Float
)
```

---

## 7. Smart Insights — Communicator Style Classification

### 7.1 Definition

A user's communication *style* is determined by the ratio of their average
session duration to their median session duration:

```
burstiness_ratio = average_session_duration / median_session_duration

if burstiness_ratio > 3.0 → "Balanced" (few long sessions)
if burstiness_ratio < 1.5 → "Burst"    (many short check-ins)
else                       → "Mixed"
```

### 7.2 Repository Implementation

```kotlin
// UsageRepositoryImpl.kt
override suspend fun computeSmartInsights(fromEpoch: Long, toEpoch: Long): SmartInsights {
    val durations = sessionDao.getAllDurationsMs(fromEpoch, toEpoch)
    if (durations.isEmpty()) return SmartInsights.Unavailable

    val avg    = durations.average()
    val median = durations.sorted().let { s ->
        if (s.size % 2 == 0) (s[s.size / 2 - 1] + s[s.size / 2]) / 2.0
        else s[s.size / 2].toDouble()
    }
    val ratio = if (median > 0) avg / median else 1.0

    val style = when {
        ratio > 3.0 -> CommunicatorStyle.BALANCED
        ratio < 1.5 -> CommunicatorStyle.BURST
        else        -> CommunicatorStyle.MIXED
    }

    val longestSession = sessionDao.getLongestSession(fromEpoch, toEpoch)

    return SmartInsights.Available(
        style             = style,
        averageDurationMs = avg.toLong(),
        medianDurationMs  = median.toLong(),
        totalSessions     = durations.size,
        longestContactName = longestSession?.contactName,
        longestDurationMs  = longestSession?.durationMs ?: 0L
    )
}
```

### 7.3 DAO Helpers

```kotlin
@Query("""
    SELECT duration_ms FROM sessions
    WHERE end_timestamp IS NOT NULL
      AND start_timestamp BETWEEN :from AND :to
      AND duration_ms > 0
""")
suspend fun getAllDurationsMs(from: Long, to: Long): List<Long>

@Query("""
    SELECT contact_name, MAX(duration_ms) AS durationMs FROM sessions
    WHERE end_timestamp IS NOT NULL
      AND start_timestamp BETWEEN :from AND :to
    GROUP BY contact_name
    ORDER BY durationMs DESC
    LIMIT 1
""")
suspend fun getLongestSession(from: Long, to: Long): LongestSessionDto?
```

---

## 8. "The Entertainer" — Status View Ranking

A separate ranking that uses only `session_type = 'STATUS'` rows, ordered
by total time spent watching each contact's statuses.

```kotlin
@Query("""
    SELECT
        contact_name,
        SUM(duration_ms)  AS total_duration_ms,
        COUNT(*)          AS view_count
    FROM sessions
    WHERE session_type = 'STATUS'
      AND end_timestamp IS NOT NULL
      AND start_timestamp BETWEEN :from AND :to
    GROUP BY contact_name
    ORDER BY total_duration_ms DESC
    LIMIT :limit
""")
suspend fun getTopStatusViewers(from: Long, to: Long, limit: Int = 5): List<StatusViewDto>
```

---

## 9. Yearly Aggregations (Wrapped Inputs)

```kotlin
// Run all aggregations in parallel — avoids sequential DB waits
override suspend fun buildYearlyReport(year: Int): YearlyReportData {
    val range = yearEpochRange(year)

    return coroutineScope {
        val topContacts   = async { sessionDao.getTopContactsByScore(range.first, range.second) }
        val topStatus     = async { sessionDao.getTopStatusViewers(range.first, range.second) }
        val monthlyBreak  = async { sessionDao.getDurationByMonth(range.first, range.second) }
        val insights      = async { computeSmartInsights(range.first, range.second) }
        val longestChat   = async { sessionDao.getLongestSession(range.first, range.second) }
        val totalTime     = async { sessionDao.getTotalDurationMs(range.first, range.second) }

        YearlyReportData(
            topContacts         = topContacts.await(),
            topStatusContact    = topStatus.await().firstOrNull(),
            monthlyBreakdown    = monthlyBreak.await(),
            smartInsights       = insights.await(),
            longestChat         = longestChat.await(),
            totalTimeMs         = totalTime.await() ?: 0L,
            year                = year,
        )
    }
}

private fun yearEpochRange(year: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(year, Calendar.JANUARY, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0)
    val start = cal.timeInMillis
    cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
    val end = cal.timeInMillis
    return start to end
}
```

---

## 10. Data Contracts

```kotlin
data class YearlyReportData(
    val year: Int,
    val totalTimeMs: Long,
    val topContacts: List<ContactScoreDto>,
    val topStatusContact: StatusViewDto?,
    val monthlyBreakdown: List<MonthlyDurationDto>,
    val smartInsights: SmartInsights,
    val longestChat: LongestSessionDto?,
)

sealed class SmartInsights {
    object Unavailable : SmartInsights()
    data class Available(
        val style: CommunicatorStyle,
        val averageDurationMs: Long,
        val medianDurationMs: Long,
        val totalSessions: Int,
        val longestContactName: String?,
        val longestDurationMs: Long,
    ) : SmartInsights()
}

enum class CommunicatorStyle { BURST, BALANCED, MIXED }
```

---

## 11. Repository Interface (Dependency Inversion)

```kotlin
// domain/UsageRepository.kt — what ViewModels depend on
interface UsageRepository {
    suspend fun getTopContacts(fromEpoch: Long, toEpoch: Long, limit: Int = 10): List<ContactScoreDto>
    suspend fun computeSmartInsights(fromEpoch: Long, toEpoch: Long): SmartInsights
    suspend fun buildYearlyReport(year: Int): YearlyReportData
    fun observeTodayTopContacts(): Flow<List<ContactScoreDto>>
}

// data/UsageRepositoryImpl.kt — what Hilt provides
class UsageRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : UsageRepository { ... }
```

Bind in Hilt:
```kotlin
@Module @InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindUsageRepo(impl: UsageRepositoryImpl): UsageRepository
}
```

---

## 12. Gotchas

- **Exclude open sessions.** Any `WHERE end_timestamp IS NOT NULL` guard is mandatory — open sessions have no `duration_ms` and will skew averages toward zero.
- **Minimum duration filter.** Sessions under ~2 seconds are almost certainly accidental taps. Apply `AND duration_ms > 2000` in aggregation queries.
- **Contact name casing.** WhatsApp can return the same contact as "Alice" and "alice" on different builds. Normalise with `LOWER(contact_name)` in SQL or in the Repository before ranking.
- **The FREQUENCY_WEIGHT constant is tunable.** Start at 30, but expose it as a configurable constant. As the data set grows, run a retrospective to see whether higher or lower values better match user-perceived closeness.
- **Empty state.** `buildYearlyReport` must gracefully return a minimal `YearlyReportData` with zeroed fields when no sessions exist for the year — never crash the UI.
