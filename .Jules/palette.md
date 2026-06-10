## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-24 - Semantics for Custom Selected States
**Learning:** Screen readers do not automatically infer selection states from visual changes like background color. Also, adding semantics *before* the `.clickable` modifier can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier on interactive components with custom selection states.
