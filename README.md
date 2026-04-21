# MuzzChat

Android chat technical exercise. One screen, two fixed users, persistent messages.

**Min SDK 29 · Kotlin · Jetpack Compose · Room · Koin**

---

## Architecture

Single-module MVI with strict unidirectional data flow.

```
┌─────────────────────────────────┐
│         Presentation            │
│   View  ──Intent──▶  ViewModel  │
│    ▲                     │      │
│    └──────UiState─────────┘     │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│            Domain               │
│       UseCases · Models         │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│             Data                │
│      Repository · Room          │
└─────────────────────────────────┘
```

**Single-module was deliberate.** One screen, two fixed users — splitting into `:data` / `:feature` modules would add Gradle overhead with no meaningful isolation boundary.

---

## Stack

| Category | Libraries |
|---|---|
| UI | Jetpack Compose · Material Design 3 · Material Icons Extended |
| Persistence | Room · KSP (annotation processing) |
| DI | Koin BOM — android + compose artifacts |
| Async | Kotlin Coroutines · StateFlow |
| Testing | JUnit 4 · MockK · Turbine · kotlinx-coroutines-test |

**Non-trivial choices:**

| Decision | Rationale |
|---|---|
| Koin over Hilt | Hilt requires Dagger and more setup. For a single-module, single-screen app the entire DI graph fits in one readable file. Hilt would be the right call at multi-module scale. |
| KSP over KAPT | Faster incremental builds and first-class Kotlin support. Room has supported KSP since 2.4 with no trade-offs. |
| `java.time` | minSdk 29 covers all `java.time` APIs natively — no core library desugaring. `SimpleDateFormat` is mutable and thread-unsafe. |

---

## Key decisions

### `BuildChatItemsUseCase` — pure Kotlin, JVM-testable

`List<Message> → List<ChatItem>`, no `Context`, no Android SDK type. Runs in plain JUnit without Robolectric or an instrumented device. All grouping logic lives in one place; the ViewModel just calls it and forwards the result into state.

The two decoration rules look in opposite directions:

- **DateHeader** — looks *back*. Insert before message N if there is no previous message, or the gap to N−1 is ≥ 1 hour.
- **isGroupedWithNext** — looks *forward*. Set `true` on message N when N+1 exists, shares the sender, and arrives within 20 seconds.

This asymmetry means a `map` or `fold` doesn't fit; the use case iterates with `forEachIndexed` and reads both neighbours via `getOrNull(index ± 1)`.

### `BaseViewModel` — equality short-circuit

```kotlin
protected fun submitState(reducer: State.() -> State) {
    val new = _uiState.value.reducer()
    if (new == currentState) return   // skips emission on no-op
    _uiState.value = new
}
```

Prevents redundant `StateFlow` emissions and downstream recompositions when an action produces no observable change in state.

### No `UiEffect`

Every piece of information the UI needs — `items`, `inputText`, `currentUser` — is durable state. There are no one-shot side effects (navigation, snackbars, permissions), so a `Channel<Effect>` would add lifecycle risk with no benefit. `UiEffect` is introduced only when the screen actually needs it.

### Optimistic input clear

The input field is cleared *before* the `repository.insert()` coroutine launches. This removes the visible flicker where text lingers until the DB write completes. The trade-off is that a failed write silently drops the message — acceptable for a demo; production code would catch the exception and restore the text.

### IME + edge-to-edge

`enableEdgeToEdge()` draws behind both bars. `Scaffold.contentWindowInsets` is pinned to `systemBars` only; `imePadding()` is applied on the inner `Column`. This slides the entire input area above the soft keyboard without a `windowSoftInputMode` change in the manifest and avoids double-padding from the scaffold consuming the IME inset.

---

## Testing

`BuildChatItemsUseCase`, `MessageRepository`, and `ChatViewModel` are covered by unit tests running on the JVM.

- **Turbine** for `StateFlow` / `Flow` assertions — avoids the `launch` + `Channel` plumbing of manual collection.
- **MockK** for all test doubles — no hand-written fakes.
- **`StandardTestDispatcher`** + `advanceUntilIdle()` for deterministic coroutine control.
- Test naming: `` `given X when Y then Z` `` with `// GIVEN / WHEN / THEN` sections in the body.

---

## What I Would Have Done With More Time

The following are conscious trade-offs made to keep scope focused. Each point describes the current limitation and how it would be addressed in a production app.

**Pagination.** `MessageDao.observeAll()` loads the full message history on every emission. For a demo this is fine, but a real chat with hundreds of messages would replace this with a `PagingSource` backed query and wire `LazyPagingItems` into the Compose layer — keeping memory flat regardless of history size.

**Error handling on writes.** A failed `repository.insert()` is currently silently dropped. With more time I'd wrap the coroutine in a try/catch, emit an error state, and restore the input text so the user can retry without losing their message.

**Compose UI tests.** Business logic is covered by unit tests, but there are no instrumented tests asserting that `DateSectionHeader` and bubble grouping render correctly end-to-end. I'd add a Compose test that seeds a known message list and verifies the rendered output.

**Per-bubble timestamps.** The design shows them. I'd add them conditionally — visible only on the last message of a group — to match the spec without visual noise.

**Splash screen guard.** `DatabaseSeeder` currently runs fire-and-forget in a detached `CoroutineScope(Dispatchers.IO)`. ~10 inserts finishes within one frame in practice, but a larger seed would need `SplashScreen.setKeepOnScreenCondition` to hold the splash until the data is ready.

**Fixed users, no identity.** Sender switching via button is intentional per spec. A real app would replace this with proper authentication and map each message to an account, making the conversation model server-ready.
