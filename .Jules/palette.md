## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-05-15 - Interactive Component Selection Semantics
**Learning:** Screen readers do not automatically infer selected state from visual changes in custom Jetpack Compose components. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** When implementing custom accessible selection states, use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier.
