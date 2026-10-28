## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-28 - Explicit Selection State Semantics
**Learning:** Screen readers do not automatically infer selection states from visual changes alone on custom interactive components.
**Action:** Use `.clearAndSetSemantics { selected = isSelected }` explicitly after the `.clickable` modifier to ensure TalkBack accurately announces the selected state without overriding the click action.
