## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-20 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose. Modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier on interactive components to ensure selection states are correctly announced without breaking click actions.
