## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-04 - Accessible Selection States in Compose
**Learning:** When adding semantic descriptions to custom interactive components using `.clearAndSetSemantics`, placing it *before* the `.clickable` modifier overrides and removes the TalkBack click action entirely. Screen readers also do not automatically infer selection state from background colors or text color changes.
**Action:** Always place `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to preserve functionality while ensuring visually selected states are conveyed properly.
