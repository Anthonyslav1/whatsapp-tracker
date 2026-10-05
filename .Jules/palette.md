## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Selected State in Compose Semantics
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose elements unless semantic properties are explicitly provided. Modifying semantics *before* the `.clickable` modifier can inadvertently override the TalkBack click action.
**Action:** When creating custom selection states on interactive components, add an explicit semantic block `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` strictly *after* the `.clickable` modifier to ensure TalkBack properly reads the selection state without destroying interaction functionality.
