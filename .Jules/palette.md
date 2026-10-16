## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-16 - Semantics Order in Compose
**Learning:** TalkBack selection state can be overridden if semantics are not ordered correctly relative to interactive modifiers like `.clickable`.
**Action:** When implementing custom accessible selection states, explicitly place `.clearAndSetSemantics { selected = isSelected }` *after* the `.clickable` modifier to ensure screen readers announce selection correctly without breaking the click action.
