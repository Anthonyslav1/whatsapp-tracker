## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-12 - Explicit Selection Semantics
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. Furthermore, modifying semantics *before* the `.clickable` modifier can override the default click action for TalkBack.
**Action:** When implementing custom accessible selection states, compute the description first, then use `.clearAndSetSemantics { contentDescription = description; selected = isSelected }` placed explicitly *after* the `.clickable` modifier.
