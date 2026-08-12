## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessible Selection States in Compose
**Learning:** By default, Android TalkBack doesn't automatically infer selection from visual changes in custom interactive components. Furthermore, modifying semantics before `.clickable` can override the click action for screen readers.
**Action:** Implement custom accessible selection states using explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier to ensure both selection state and click actions are correctly announced.
