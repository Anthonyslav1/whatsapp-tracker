## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-21 - Explicit Semantic Selection
**Learning:** Android TalkBack does not automatically infer selection state from visual style changes (e.g., background color) on custom interactive elements.
**Action:** Use `.clearAndSetSemantics { selected = isSelected }` placed explicitly *after* the `.clickable` modifier to ensure screen readers correctly announce the selected state.
