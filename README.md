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
