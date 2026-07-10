## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-24 - Custom Accessible Selection States
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose elements. Modifying semantics *before* a `.clickable` modifier can sometimes override the click action for TalkBack.
**Action:** Implement custom selected semantics explicitly by appending `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` *after* the `.clickable` modifier. Always evaluate dynamic text outside the `clearAndSetSemantics` block.
