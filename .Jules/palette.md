## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Explicit Selection Semantics in Jetpack Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. If semantics are placed before `.clickable`, they override the click action for TalkBack.
**Action:** Implement explicit semantic properties (`.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) on interactive components, strictly *after* the `.clickable` modifier to ensure correct TalkBack announcements.
