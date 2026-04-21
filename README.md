# MuzzChat

A chat interface for Android built as a technical exercise. Two fixed users, persistent messages, observable architecture.

---

## Architecture

Single-module MVI. State flows in one direction; the View never mutates it directly.

```
┌─────────────────────────────────┐
│         Presentation            │
│                                 │
│   View  ──Intent──▶  ViewModel  │
│    ▲                     │      │
│    └──────UiState─────────┘     │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│            Domain               │
│                                 │
│       UseCases · Models         │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│             Data                │
│                                 │
│      Repository · Room          │
└─────────────────────────────────┘
```

The choice to stay single-module was deliberate. This is one screen between two fixed users — splitting it into feature and data modules would add Gradle complexity and indirection with no real payoff.

---

## Stack

- **Jetpack Compose** — UI
- **Room + Flow** — persistent storage with observable queries
- **Koin** — dependency injection

---

## Key decisions

### BuildChatItemsUseCase

`BuildChatItemsUseCase` is a pure Kotlin class with a single `operator fun invoke(messages: List<Message>): List<ChatItem>`. It has no Android dependencies — no `Context`, no `ViewModel`, nothing from the SDK. This means it can be unit-tested with plain JUnit, without Robolectric or an instrumented device. The ViewModel just calls it and forwards the result into state; all the grouping logic stays in one testable place.

The two rules it applies are asymmetric in the direction they look:

- **DateHeader** looks *backward*. To know whether to insert a header before message N, you need to know when message N−1 was sent. If there is no previous message, or the gap is ≥ 1 hour, a header is inserted.
- **isGroupedWithNext** looks *forward*. To know whether message N should have reduced spacing below it, you need to know when message N+1 will arrive and whether it comes from the same sender. If the next message exists, shares the sender, and arrives within 20 seconds, the flag is set to `true`.

This look-ahead vs look-back asymmetry is why a simple `fold` or `map` isn't enough — the use case iterates with `forEachIndexed` and reads both `index - 1` and `index + 1` via `getOrNull`.

### State management

The presentation layer uses a small `UiState` / `UiAction` contract with a generic `BaseViewModel`.
The View dispatches actions, and all state mutations go through `submitState { copy(...) }`, which keeps updates centralized and predictable.

`submitState` short-circuits when the reducer returns an equal state, avoiding redundant `StateFlow` emissions and unnecessary recomposition.

There is no `UiEffect` channel because this screen has no one-shot side effects such as navigation, snackbars, or permission requests.
Everything the UI needs is durable state: the rendered chat items, the current input text, and the active sender.

### Dependency injection

The app uses Koin with a single application module because the project is intentionally small: one screen, one repository, one database, one ViewModel.
A single module keeps the dependency graph easy to read and avoids unnecessary structure.

Koin was chosen over Hilt because the setup cost is lower for a small exercise and the graph is simple enough that explicit module wiring stays readable.
This keeps the project lightweight while still giving proper dependency construction for the database, repository, use case, and ViewModel.

### Screen — LazyColumn and IME

`MainActivity` calls `enableEdgeToEdge()`, which draws behind both the status bar and the navigation bar. To prevent the `Scaffold` from consuming the IME inset, `contentWindowInsets` is pinned to `WindowInsets.systemBars` only. The `Column` that holds the message list, sender selector, and input field applies `imePadding()` independently — this causes the entire input area to slide above the soft keyboard without requiring any `windowSoftInputMode` change in the manifest.

Auto-scroll is driven by `LaunchedEffect(uiState.items.size)`: any time a message is added the list animates to its last index. Keying the effect on `items.size` means it only fires on list growth, not on every recomposition.

---

## Limitations and trade-offs

**Seed data runs fire-and-forget.** `DatabaseSeeder.seedIfEmpty()` is launched in a detached `CoroutineScope(Dispatchers.IO)` from `Application.onCreate()`. For ~10 SQLite inserts this completes well within a single frame, so in practice the UI never renders an empty list on first launch. A production app with a larger or slower seed would need to hold the splash screen via `SplashScreen.setKeepOnScreenCondition` until the operation finishes.

**No error handling on seed.** If the database insert fails (e.g. disk full), the exception is silently swallowed. The app still opens but with an empty chat. This is acceptable for demo data — a real app would log the error or surface it.

**Two fixed users, no auth.** The sender is toggled via a UI button rather than any form of identity. This is intentional per the spec but means the "Reply as Sarah" pattern would not survive a multi-device or server-backed scenario without rework.

**No pagination.** `MessageDao.observeAll()` loads the full message history into memory on every emission. For a demo this is fine; a production chat would use `PagingSource` to avoid loading thousands of rows at once.

---

## What I'd do with more time

**Pagination.** Replace the `Flow<List<MessageEntity>>` query with a `PagingSource` and wire it to `LazyPagingItems` in the Compose layer.

**Proper splash screen guard.** If the seed dataset grew, I'd add `core-splashscreen` and hold the screen with `setKeepOnScreenCondition` rather than relying on timing.

**UI tests.** The business logic is covered by unit tests, but there are no Compose UI tests. I'd add a test that seeds a known message list and asserts that `DateSectionHeader` and grouped spacing are rendered correctly.

**Message timestamps.** Currently messages show no per-message timestamp. The design shows them; I'd add a `Text` below each bubble that appears conditionally (e.g. only on the last message of a group).
