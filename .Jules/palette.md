## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Compose Custom Selection States Semantics
**Learning:** Screen readers do not automatically infer selection states from visual changes. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack in Jetpack Compose.
**Action:** Use explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier to ensure TalkBack announces the state properly without breaking the click action.
