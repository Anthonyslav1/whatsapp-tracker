## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-09-26 - Explicit Selection Semantics in Jetpack Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Always use explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = "Day X"; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier when building custom accessible selection states.
