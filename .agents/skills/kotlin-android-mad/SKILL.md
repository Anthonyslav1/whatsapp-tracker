---
name: kotlin-android-mad
description: >
  Use this skill for any Modern Android Development (MAD) task in Kotlin —
  writing Jetpack Compose UI, ViewModels, Room DAOs, Repositories, Hilt modules,
  WorkManager tasks, or unit/integration tests. Trigger on requests involving
  Compose screens or components, coroutines or Flow pipelines, Room persistence,
  Hilt dependency injection, MVVM/Clean Architecture layering, or reactive state
  management (StateFlow, SharedFlow). Do NOT use for Gradle build scripts, Ktor/
  Spring Boot backends, or any legacy Android paradigm (AsyncTask, XML layouts,
  findViewById, manual ViewModel factories).
---

This skill produces idiomatic, production-grade Modern Android code in Kotlin. Every output must be reactive-first, null-safe, and structurally clean — no shortcuts, no legacy patterns, no main-thread blocking.

The user provides a feature request, a layer to work in (UI / Domain / Data), or an existing code snippet to refactor or test. They may also provide existing class names, repository interfaces, or DI bindings to integrate against.

---

## Architectural Decision Framework

Before writing a single line, identify the layer and enforce its contract:

**UI Layer (Compose + ViewModel)**
- Composables must be stateless; all state lives in the ViewModel.
- Collect `StateFlow` or `SharedFlow` using `collectAsStateWithLifecycle()` — never `collectAsState()` alone (lifecycle-unsafe).
- Side effects belong in `LaunchedEffect`, `SideEffect`, or `DisposableEffect` — never inline in the composition.
- Navigation events flow as `SharedFlow<UiEvent>` — never exposed as mutable state booleans.

**Domain Layer (UseCases + Interfaces)**
- Zero Android framework imports. Pure Kotlin only.
- Define repository contracts as interfaces here; implementations live in the Data layer.
- Use `suspend fun` for one-shot operations; `Flow<T>` for reactive streams.
- UseCases are single-responsibility: one public `operator fun invoke(...)`.

**Data Layer (Room + Retrofit + DataStore)**
- DAOs return `Flow<T>` for queries that drive UI; `suspend fun` for mutations.
- Repository implementations map data-layer models to domain models before returning.
- Never expose Room entities or Retrofit DTOs above the Data layer.
- Use `@Transaction` for multi-table reads to prevent partial emissions.

**DI Layer (Hilt)**
- Scope precisely: `@Singleton` for repositories and network clients; `@ViewModelScoped` for use-case instances that hold ViewModel-level state.
- Use `@Binds` over `@Provides` when injecting interface implementations — generates less bytecode.
- Never inject into Composables directly; inject into ViewModels only.

---

## Mandatory Code Rules

These are non-negotiable. Violations make output invalid regardless of functional correctness.

1. **No blocking calls on Main.** Any I/O or CPU-heavy work uses `withContext(Dispatchers.IO)` or `Dispatchers.Default`.
2. **No raw coroutine leaks.** All coroutines launched from ViewModels use `viewModelScope`. Data layer coroutines use the injected `CoroutineDispatcher`, never `GlobalScope`.
3. **Strict null-safety.** Prefer `?.let`, `?:`, and `requireNotNull()` over `!!`. Document every intentional `!!` use with a comment explaining the invariant.
4. **No hardcoded strings.** UI text belongs in `strings.xml`; magic numbers belong in named constants or `Dimensions.kt`.
5. **No framework leakage across layers.** `Context`, `Activity`, `Fragment`, and `View` must never appear in Domain or Data layer classes.
6. **Sealed results for error handling.** Network and DB operations return `Result<T>` or a custom `sealed class Resource<T>` — never raw nullable types or thrown exceptions crossing layer boundaries.

---

## Implementation Workflow

### Step 1 — Scope the Output
Identify all files that need to change. If the request touches more than one layer, plan and state the file list before writing any code. Example:
```
Files to produce:
- data/local/dao/ShipmentDao.kt
- data/repository/ShipmentRepositoryImpl.kt
- domain/repository/ShipmentRepository.kt
- domain/usecase/GetShipmentStreamUseCase.kt
- ui/shipment/ShipmentViewModel.kt
- ui/shipment/ShipmentScreen.kt
```

### Step 2 — Define Contracts First
Write interfaces and data models before implementations. This enforces clean boundaries and makes the logic reviewable before the plumbing is written.

### Step 3 — Implement Bottom-Up
Data layer → Domain layer → ViewModel → Composable. Each layer only depends on the layer directly below it via the defined interface.

### Step 4 — Validate Before Finalising
Review each file against these checks:
- [ ] No Android imports in Domain layer
- [ ] No `StateFlow.value` reads inside Compose — only `collectAsStateWithLifecycle()`
- [ ] All coroutines are scoped and cancellable
- [ ] Room queries returning lists use `Flow`, not `suspend List<T>`
- [ ] No `@Singleton` on ViewModels
- [ ] Error paths are handled with sealed types, not silent nulls

---

## Tech Stack Assumptions (override explicitly if different)

| Concern | Library | Version Target |
|---|---|---|
| UI | Jetpack Compose | BOM 2024.x |
| State | `StateFlow` / `SharedFlow` | Coroutines 1.8.x |
| DI | Hilt | 2.51.x |
| Persistence | Room | 2.6.x |
| Networking | Retrofit + OkHttp | 2.11.x / 4.x |
| Image Loading | Coil 3 | 3.x (Compose-first) |
| Navigation | Compose Navigation | 2.7.x |
| Background | WorkManager | 2.9.x |
| Testing | JUnit 5, MockK, Turbine | latest stable |

---

## Output Format

- Each file begins with its full target path as a comment: `// ui/shipment/ShipmentScreen.kt`
- Files are separated by a horizontal rule (`---`) with a one-line description of what changed and why.
- If a decision has a non-obvious tradeoff (e.g., choosing `SharedFlow` over `StateFlow` for events), add a `// DECISION:` inline comment explaining the reasoning.
- Imports are fully qualified and grouped: Android → Jetpack → Third-party → Project.

---

## Examples

### Example 1: Reactive Room DAO with Junction Table

**Request:** "Give me a DAO that fetches all shipments with their associated compliance tags. It must reactively update the UI when either table changes."

**Thought process:** A junction table join requires `@Transaction` to prevent partial reads mid-write. The return type must be `Flow` so Room can push updates automatically.

```kotlin
// data/local/dao/ShipmentDao.kt
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import com.origintrace.data.local.model.ShipmentWithTags

@Dao
interface ShipmentDao {

    /**
     * Emits the full list of shipments with their compliance tags whenever
     * either [shipments] or [shipment_tags] tables are mutated.
     *
     * @Transaction prevents partial reads across the junction join.
     */
    @Transaction
    @Query("""
        SELECT s.* FROM shipments s
        INNER JOIN shipment_tags st ON s.id = st.shipment_id
        ORDER BY s.created_at DESC
    """)
    fun observeShipmentsWithTags(): Flow<List<ShipmentWithTags>>

    @Query("UPDATE shipments SET compliance_status = :status WHERE id = :id")
    suspend fun updateComplianceStatus(id: String, status: String)
}
```

---

### Example 2: ViewModel with Sealed UI State

**Request:** "Build a ViewModel for the Shipment Detail screen. It should load shipment data, expose loading/success/error states, and handle a 'mark as verified' action."

**Thought process:** A sealed `UiState` models all render states. The action is a `suspend` function called inside `viewModelScope`. Navigation/toast events are `SharedFlow` to avoid re-delivery on recomposition.

```kotlin
// ui/shipment/detail/ShipmentDetailViewModel.kt
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.origintrace.domain.usecase.GetShipmentDetailUseCase
import com.origintrace.domain.usecase.VerifyShipmentUseCase
import com.origintrace.domain.model.Shipment
import javax.inject.Inject

sealed class ShipmentDetailUiState {
    data object Loading : ShipmentDetailUiState()
    data class Success(val shipment: Shipment) : ShipmentDetailUiState()
    data class Error(val message: String) : ShipmentDetailUiState()
}

sealed class ShipmentDetailEvent {
    data object VerificationSuccess : ShipmentDetailEvent()
    data class ShowSnackbar(val message: String) : ShipmentDetailEvent()
}

@HiltViewModel
class ShipmentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getShipmentDetail: GetShipmentDetailUseCase,
    private val verifyShipment: VerifyShipmentUseCase,
) : ViewModel() {

    private val shipmentId: String = checkNotNull(savedStateHandle["shipmentId"])

    private val _uiState = MutableStateFlow<ShipmentDetailUiState>(ShipmentDetailUiState.Loading)
    val uiState: StateFlow<ShipmentDetailUiState> = _uiState.asStateFlow()

    // DECISION: SharedFlow (not StateFlow) — events must not replay on recomposition.
    private val _events = MutableSharedFlow<ShipmentDetailEvent>()
    val events: SharedFlow<ShipmentDetailEvent> = _events.asSharedFlow()

    init {
        observeShipment()
    }

    private fun observeShipment() {
        getShipmentDetail(shipmentId)
            .map<Shipment, ShipmentDetailUiState> { ShipmentDetailUiState.Success(it) }
            .catch { e -> emit(ShipmentDetailUiState.Error(e.localizedMessage ?: "Unknown error")) }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun onVerifyClicked() {
        viewModelScope.launch {
            verifyShipment(shipmentId)
                .onSuccess { _events.emit(ShipmentDetailEvent.VerificationSuccess) }
                .onFailure { _events.emit(ShipmentDetailEvent.ShowSnackbar("Verification failed")) }
        }
    }
}
```

---

### Example 3: Unit Test with Turbine and MockK

**Request:** "Write a unit test for `ShipmentDetailViewModel`. Test that the state is `Loading` initially, transitions to `Success` on data emission, and emits a `VerificationSuccess` event on `onVerifyClicked()`."

**Thought process:** Use `TestCoroutineDispatcher` via `runTest`. Use Turbine's `test {}` block to assert `Flow` emissions in sequence. Mock use cases with MockK.

```kotlin
// ui/shipment/detail/ShipmentDetailViewModelTest.kt
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import com.origintrace.domain.usecase.GetShipmentDetailUseCase
import com.origintrace.domain.usecase.VerifyShipmentUseCase

class ShipmentDetailViewModelTest {

    private val getShipmentDetail: GetShipmentDetailUseCase = mockk()
    private val verifyShipment: VerifyShipmentUseCase = mockk()

    @Test
    fun `initial state is Loading, transitions to Success on emission`() = runTest {
        val fakeShipment = Shipment(id = "abc", status = "PENDING")
        every { getShipmentDetail("abc") } returns flowOf(fakeShipment)

        val vm = buildViewModel()

        vm.uiState.test {
            // Initial emission after Flow maps
            val state = awaitItem()
            assertTrue(state is ShipmentDetailUiState.Success)
            assertEquals("abc", (state as ShipmentDetailUiState.Success).shipment.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onVerifyClicked emits VerificationSuccess event on success`() = runTest {
        every { getShipmentDetail("abc") } returns flowOf(Shipment(id = "abc", status = "PENDING"))
        coEvery { verifyShipment("abc") } returns Result.success(Unit)

        val vm = buildViewModel()

        vm.events.test {
            vm.onVerifyClicked()
            assertEquals(ShipmentDetailEvent.VerificationSuccess, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildViewModel() = ShipmentDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("shipmentId" to "abc")),
        getShipmentDetail = getShipmentDetail,
        verifyShipment = verifyShipment,
    )
}
```

---

## Common Anti-Patterns — Never Produce These

| Anti-Pattern | Correct Replacement |
|---|---|
| `GlobalScope.launch { }` | `viewModelScope.launch { }` |
| `flow.collectAsState()` in Compose | `flow.collectAsStateWithLifecycle()` |
| `runBlocking { }` in ViewModels | `viewModelScope.launch { }` |
| `LiveData` in new code | `StateFlow` |
| Mutable state exposed from ViewModel (`var`) | Private `MutableStateFlow` + public `asStateFlow()` |
| Room entity returned from Repository | Map to domain model before crossing layer |
| `@Singleton` on a ViewModel | `@HiltViewModel` only — Hilt scopes it correctly |
| `try/catch` swallowing errors silently | `sealed class Resource<T>` propagated to UI |
| Strings hardcoded in Composables | `stringResource(R.string.*)` |
| `!!` without invariant comment | `requireNotNull(x) { "reason" }` |
