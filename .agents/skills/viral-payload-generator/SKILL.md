---
name: viral-payload-generator
description: >
  Use this skill when building or modifying the Yearly Wrapped report, the
  shareable "Meta" summary, any swipeable Wrapped card UI, the share Intent
  flow, or any Compose screen that presents end-of-year statistics in a visual,
  engaging format. Triggers for work on YearlyReportViewModel, YearlyReportData,
  WrappedScreen, WrappedBanner, card-based pager UI, share sheet integration,
  fun-fact generation, or "Share Your Meta" button logic. If the user mentions
  "Wrapped", "year in review", "share stats", "shareable summary", "viral
  sharing", "Top 5", "Most Active Month", or "fun facts", always use this skill.
---

# Viral Payload Generator — The Storyteller

## 1. Overview

**Mission:** Compile a year's worth of session rows into a beautiful, swipeable,
and instantly shareable "Meta" report — Ravdesk's primary organic growth engine.
Every user who shares their stats is a free billboard.

**Core principle:** Data tells, story sells. Raw numbers ("You spent 14,220
minutes on WhatsApp") are forgettable. Framed narratives ("That's 9.8 days of
your 2024 — enough to fly to the Moon and halfway back") are shareable.

---

## 2. When to Use

- Building the `YearlyReportScreen` Compose UI (swipeable card pager)
- Implementing `YearlyReportViewModel` (fetching + formatting Wrapped data)
- Writing the plain-text "Share Your Meta" payload and wiring the Android Intent
- Adding new Wrapped "cards" (slides) to the swipeable sequence
- Writing fun-fact generation logic
- Implementing type-safe navigation to/from the Wrapped screen

## 3. When NOT to Use

- Computing the raw aggregations (totals, rankings) → use `engagement-scoring` skill
- Parsing WhatsApp UI events → use `contextual-ingestion` skill
- Session persistence and crash recovery → use `resilient-persistence` skill

---

## 4. Architecture

```
YearlyReportData (from UsageRepository)
    │
    ▼
YearlyReportViewModel.kt
    │  buildWrappedCards()        ← transforms data into ordered card list
    │  buildSharePayload()        ← formats plain-text share string
    │  StateFlow<WrappedUiState>
    ▼
YearlyReportScreen.kt            ← Compose HorizontalPager
    │
    ├── WrappedCard.kt           ← Stateless composable per slide
    └── ShareButton              ← Fires Android share Intent
```

---

## 5. ViewModel — Data Compilation

```kotlin
// presentation/yearly/YearlyReportViewModel.kt
@HiltViewModel
class YearlyReportViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WrappedUiState>(WrappedUiState.Loading)
    val uiState: StateFlow<WrappedUiState> = _uiState.asStateFlow()

    fun loadReport(year: Int) {
        viewModelScope.launch {
            _uiState.value = WrappedUiState.Loading
            try {
                val data  = usageRepository.buildYearlyReport(year)
                val cards = buildWrappedCards(data)
                val share = buildSharePayload(data)
                _uiState.value = WrappedUiState.Ready(cards, share)
            } catch (e: Exception) {
                _uiState.value = WrappedUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // -----------------------------------------------------------------------
    // Card Builder — ordered list of slides
    // -----------------------------------------------------------------------
    private fun buildWrappedCards(data: YearlyReportData): List<WrappedCard> {
        val cards = mutableListOf<WrappedCard>()

        // Slide 1: Total time hero stat
        cards += WrappedCard.HeroStat(
            headline = "Your ${data.year} in WhatsApp",
            value    = formatDuration(data.totalTimeMs),
            subline  = funFactFromMs(data.totalTimeMs),
        )

        // Slide 2: #1 Best Friend
        data.topContacts.firstOrNull()?.let { top ->
            cards += WrappedCard.BestFriend(
                contactName   = top.contactName,
                totalTime     = formatDuration(top.totalDurationMs),
                sessionCount  = top.sessionCount,
            )
        }

        // Slide 3: Full Top 5
        if (data.topContacts.size >= 2) {
            cards += WrappedCard.TopFive(contacts = data.topContacts.take(5))
        }

        // Slide 4: Most Active Month
        val peakMonth = data.monthlyBreakdown.maxByOrNull { it.totalDurationMs }
        if (peakMonth != null) {
            cards += WrappedCard.ActiveMonth(
                monthLabel   = monthName(peakMonth.month),
                totalTime    = formatDuration(peakMonth.totalDurationMs),
            )
        }

        // Slide 5: The Entertainer (top status viewer)
        data.topStatusContact?.let { status ->
            cards += WrappedCard.Entertainer(
                contactName  = status.contactName,
                viewCount    = status.viewCount,
                totalTime    = formatDuration(status.totalDurationMs),
            )
        }

        // Slide 6: Smart Insight — Communication Style
        val insights = data.smartInsights
        if (insights is SmartInsights.Available) {
            cards += WrappedCard.CommunicatorStyle(
                style       = insights.style,
                description = styleDescription(insights),
            )
        }

        // Slide 7: Longest Chat Award
        data.longestChat?.let { chat ->
            cards += WrappedCard.LongestChat(
                contactName = chat.contactName,
                duration    = formatDuration(chat.durationMs),
            )
        }

        // Final slide: Share CTA
        cards += WrappedCard.ShareCta(year = data.year)

        return cards
    }
}
```

---

## 6. Fun Fact Generator

The fun fact converts raw milliseconds into a surprising real-world comparison.
This is what makes the stat shareable — nobody reposts "14,220 minutes", but
they will share "9.8 days — enough to binge all of Breaking Bad 4 times."

```kotlin
private fun funFactFromMs(ms: Long): String {
    val minutes = ms / 60_000
    val hours   = minutes / 60.0
    val days    = hours / 24.0

    return when {
        days >= 30  -> "That's over a month of your life. 👀"
        days >= 7   -> "That's ${String.format("%.1f", days)} days — " +
                       "enough to watch every Lord of the Rings film ${(hours / 9).toInt()} times."
        hours >= 48 -> "${String.format("%.1f", days)} days — " +
                       "enough to fly around the world ${(hours / 53).toInt()} times."
        hours >= 24 -> "Over a full day and night of chatting in a single year."
        hours >= 10 -> "${hours.toInt()} hours — a solid work day, but way more fun."
        else        -> "${minutes} minutes. Just getting started."
    }
}

private fun styleDescription(insights: SmartInsights.Available): String = when (insights.style) {
    CommunicatorStyle.BURST    ->
        "You're a Burst Communicator — ${insights.totalSessions} quick check-ins, " +
        "averaging ${formatDuration(insights.averageDurationMs)} each. You stay connected in rapid bursts."
    CommunicatorStyle.BALANCED ->
        "You're a Balanced Communicator — fewer, longer conversations. " +
        "Quality over quantity, every time."
    CommunicatorStyle.MIXED    ->
        "You're a Mixed Communicator — a healthy blend of quick catch-ups and deep dives."
}
```

---

## 7. Share Payload Builder (Plain Text)

The plain-text format is intentionally emoji-rich and conversational. It must
read well when pasted into a WhatsApp status or a tweet.

```kotlin
private fun buildSharePayload(data: YearlyReportData): String {
    val sb = StringBuilder()
    val top = data.topContacts.firstOrNull()

    sb.appendLine("📊 My WhatsApp ${data.year} Wrapped (via Ravdesk)")
    sb.appendLine()
    sb.appendLine("⏱ Total time: ${formatDuration(data.totalTimeMs)}")
    sb.appendLine("   ${funFactFromMs(data.totalTimeMs)}")
    sb.appendLine()

    if (top != null) {
        sb.appendLine("🏆 #1 Best Friend: ${top.contactName}")
        sb.appendLine("   ${top.sessionCount} convos · ${formatDuration(top.totalDurationMs)}")
        sb.appendLine()
    }

    val top5Names = data.topContacts.take(5).mapIndexed { i, c ->
        "${i+1}. ${c.contactName}"
    }.joinToString(" · ")
    if (top5Names.isNotBlank()) {
        sb.appendLine("💬 My Top 5: $top5Names")
        sb.appendLine()
    }

    data.topStatusContact?.let {
        sb.appendLine("👁 Most watched status: ${it.contactName} (${it.viewCount} views)")
        sb.appendLine()
    }

    val insights = data.smartInsights
    if (insights is SmartInsights.Available) {
        sb.appendLine("🧠 ${styleDescription(insights)}")
        sb.appendLine()
    }

    sb.appendLine("All data stays private on my phone 🔒")
    sb.appendLine("Try Ravdesk ↗")

    return sb.toString()
}
```

---

## 8. Share Intent (Compose)

```kotlin
// In YearlyReportScreen.kt
@Composable
fun ShareButton(payload: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, payload)
                putExtra(Intent.EXTRA_SUBJECT, "My WhatsApp Wrapped")
            }
            context.startActivity(Intent.createChooser(intent, "Share Your Meta"))
        },
        modifier = modifier
    ) {
        Icon(Icons.Default.Share, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Share Your Meta")
    }
}
```

---

## 9. Compose UI — Swipeable Card Pager

```kotlin
// presentation/yearly/YearlyReportScreen.kt
@Composable
fun YearlyReportScreen(
    viewModel: YearlyReportViewModel = hiltViewModel(),
    year: Int,
) {
    LaunchedEffect(year) { viewModel.loadReport(year) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is WrappedUiState.Loading -> FullScreenLoader()
        is WrappedUiState.Error   -> ErrorScreen(state.message)
        is WrappedUiState.Ready   -> WrappedPager(state.cards, state.sharePayload)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WrappedPager(cards: List<WrappedCard>, sharePayload: String) {
    val pagerState = rememberPagerState(pageCount = { cards.size })

    Column(Modifier.fillMaxSize()) {

        // Page indicator dots
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            cards.indices.forEach { idx ->
                Box(
                    Modifier
                        .size(if (idx == pagerState.currentPage) 10.dp else 6.dp)
                        .padding(horizontal = 3.dp)
                        .background(
                            if (idx == pagerState.currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        )
                )
            }
        }

        // Card pager
        HorizontalPager(
            state   = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            WrappedCardView(
                card         = cards[page],
                sharePayload = sharePayload,
            )
        }
    }
}
```

### Individual Card Composable

```kotlin
@Composable
fun WrappedCardView(card: WrappedCard, sharePayload: String) {
    when (card) {
        is WrappedCard.HeroStat -> HeroStatCard(card)
        is WrappedCard.BestFriend -> BestFriendCard(card)
        is WrappedCard.TopFive -> TopFiveCard(card)
        is WrappedCard.ActiveMonth -> ActiveMonthCard(card)
        is WrappedCard.Entertainer -> EntertainerCard(card)
        is WrappedCard.CommunicatorStyle -> CommunicatorStyleCard(card)
        is WrappedCard.LongestChat -> LongestChatCard(card)
        is WrappedCard.ShareCta -> ShareCtaCard(card, sharePayload)
    }
}
```

---

## 10. Data Contracts

```kotlin
// Sealed card type — one per Wrapped slide
sealed class WrappedCard {
    data class HeroStat(val headline: String, val value: String, val subline: String) : WrappedCard()
    data class BestFriend(val contactName: String, val totalTime: String, val sessionCount: Int) : WrappedCard()
    data class TopFive(val contacts: List<ContactScoreDto>) : WrappedCard()
    data class ActiveMonth(val monthLabel: String, val totalTime: String) : WrappedCard()
    data class Entertainer(val contactName: String, val viewCount: Int, val totalTime: String) : WrappedCard()
    data class CommunicatorStyle(val style: com.ravdesk.domain.CommunicatorStyle, val description: String) : WrappedCard()
    data class LongestChat(val contactName: String, val duration: String) : WrappedCard()
    data class ShareCta(val year: Int) : WrappedCard()
}

// ViewModel UI state
sealed class WrappedUiState {
    object Loading : WrappedUiState()
    data class Ready(val cards: List<WrappedCard>, val sharePayload: String) : WrappedUiState()
    data class Error(val message: String) : WrappedUiState()
}
```

---

## 11. Type-Safe Navigation Route

```kotlin
// navigation/Routes.kt
@Serializable
data class Routes {
    @Serializable object Setup
    @Serializable object Dashboard
    @Serializable data class YearlyReport(val year: Int)
}

// In NavHost:
composable<Routes.YearlyReport> { backStackEntry ->
    val route: Routes.YearlyReport = backStackEntry.toRoute()
    YearlyReportScreen(year = route.year)
}

// Navigate to Wrapped:
navController.navigate(Routes.YearlyReport(year = 2024))
```

---

## 12. Utility Formatters

```kotlin
fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours   = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60

    return when {
        hours >= 24  -> "${(hours / 24.0).let { String.format("%.1f", it) }} days"
        hours >= 1   -> "${hours}h ${minutes}m"
        else         -> "${minutes} min"
    }
}

fun monthName(month: Int): String = java.text.DateFormatSymbols().months[month - 1]
```

---

## 13. Gotchas

- **Empty state safety.** `buildWrappedCards()` must produce at least a `HeroStat` card even when all aggregations are zero. Never return an empty list — the pager will crash.
- **Contact name privacy in share text.** Consider whether to include full contact names in the shareable payload, or just first names / initials. Add a setting if needed.
- **The share Intent does not guarantee WhatsApp.** `ACTION_SEND` shows a system chooser. To deep-link specifically into WhatsApp status, use `setPackage("com.whatsapp")` — but only if WhatsApp is confirmed installed, or the Intent will throw.
- **Year boundary edge case.** If the user installs mid-year, `buildYearlyReport()` will have partial data. Always display "Data from [install date] onwards" on the HeroStat card header.
- **`HorizontalPager` requires `accompanist` or Compose Foundation 1.4+.** Confirm the project's Compose BOM version includes `ExperimentalFoundationApi` for `HorizontalPager`.
- **Fun facts age out.** Pop-culture references ("binge Breaking Bad") become dated. Store them as string resources with a version tag so they can be updated without touching logic.
