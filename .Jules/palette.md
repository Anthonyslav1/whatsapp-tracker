## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2025-02-14 - Accessible Selection States in Compose
**Learning:** Modifying semantics before `.clickable` in Jetpack Compose can override the click action for TalkBack, and screen readers do not automatically infer selection from visual changes.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier to properly implement custom accessible selection states on interactive components.
