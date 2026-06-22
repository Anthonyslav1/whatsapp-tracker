## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-22 - Date Selector Semantics Selection State
**Learning:** Screen readers do not automatically infer selection state from visual changes. Additionally, when using `.clearAndSetSemantics` on interactive components, placing it before `.clickable` can override the click action for TalkBack.
**Action:** Explicitly set `selected = isSelected` inside `.clearAndSetSemantics`, and ensure this modifier is chained *after* the `.clickable` modifier.
