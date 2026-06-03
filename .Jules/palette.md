## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Custom Component Selection Semantics
**Learning:** Screen readers do not automatically infer selection state from visual changes on custom interactive components in Jetpack Compose.
**Action:** Use `.clearAndSetSemantics { selected = isSelected }` placed explicitly *after* the `.clickable` modifier to expose selection states without overriding the click action.
