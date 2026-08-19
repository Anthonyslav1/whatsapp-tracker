## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-08-19 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection state from visual changes in custom clickable components. Setting semantics before the `.clickable` modifier can sometimes override the click action for TalkBack.
**Action:** Implement explicit semantic properties (e.g., `selected = isSelected`) and place the `.clearAndSetSemantics` modifier *after* the `.clickable` modifier on interactive elements.
