## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Selection States on Clickables
**Learning:** Screen readers do not automatically infer selection states from visual UI changes. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** When implementing custom selection states, use explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier to ensure correct speech and interaction.
