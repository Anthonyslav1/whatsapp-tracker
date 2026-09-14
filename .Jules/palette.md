## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-15 - Custom Accessible Selection States
**Learning:** By default, screen readers do not infer selection from visual changes, and applying `.clearAndSetSemantics` before the `.clickable` modifier overrides the click action.
**Action:** Use explicit semantic properties like `.clearAndSetSemantics { contentDescription = combinedText; selected = isSelected }` explicitly *after* `.clickable` so TalkBack reads both the custom text and the selection state without blocking interactions.
