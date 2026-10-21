## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2023-10-21 - Accessible Selection States in Custom Components
**Learning:** TalkBack does not automatically infer selection state from visual property changes (like background color) on custom interactive components in Jetpack Compose.
**Action:** When creating custom toggle or selection components, use `.clearAndSetSemantics { selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure screen readers announce the selection state without overriding the click action.
