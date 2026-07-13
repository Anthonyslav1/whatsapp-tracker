## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection state from visual changes in Compose interactive components.
**Action:** Use `.clearAndSetSemantics { selected = isSelected }` and place it explicitly *after* the `.clickable` modifier. Modifying semantics before `.clickable` can override the click action for TalkBack.
