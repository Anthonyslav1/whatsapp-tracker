## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics placement on Clickable modifiers
**Learning:** In Jetpack Compose, when overriding semantics for interactive components (like adding custom selection states or combined descriptions), applying `clearAndSetSemantics` *before* the `clickable` modifier can inadvertently clear TalkBack's click actions.
**Action:** Always place custom accessibility overrides (like `.clearAndSetSemantics { contentDescription = "..."; selected = ... }`) explicitly *after* the `.clickable` modifier to ensure both custom semantics and interactive actions function properly together.
