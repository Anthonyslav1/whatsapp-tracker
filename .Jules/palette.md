## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-05-17 - Semantics Modifier Ordering Relative to Clickable
**Learning:** When creating custom accessible selection states on interactive components, modifying semantics before `.clickable` can override the click action for TalkBack, and screen readers do not automatically infer selection from visual changes.
**Action:** Place `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure proper announcement without breaking interactivity.
