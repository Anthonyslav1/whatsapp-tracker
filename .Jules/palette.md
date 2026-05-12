## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-24 - Accessible Selection States in Compose
**Learning:** When building custom interactive components (like a Date Selector) with selection states in Jetpack Compose, screen readers do not automatically infer that `.background()` or color changes mean an item is "selected". Additionally, modifying semantics *before* a `.clickable` modifier can sometimes override the click action for screen readers.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` to provide a clean announcement and explicitly communicate the selection state to TalkBack. Always place this modifier *after* the `.clickable` modifier in the chain to preserve the interactive role.
