## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-08 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes. When adding custom accessible selection states to interactive components in Jetpack Compose, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure accurate reading of state without breaking interaction.
