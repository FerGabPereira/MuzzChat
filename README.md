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
