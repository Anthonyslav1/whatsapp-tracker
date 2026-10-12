## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2025-02-18 - Ensure clickable selection states are announced by TalkBack
**Learning:** By default, Android TalkBack might not clearly articulate the selection state of a custom composable that only uses visual cues for selection, even if it has a `.clickable` modifier. Also modifying semantics *before* clickable might override TalkBack's click action.
**Action:** Explicitly use `.clearAndSetSemantics { contentDescription = "[full context]"; selected = isSelected }` immediately *after* the `.clickable` modifier on custom interactive components.
