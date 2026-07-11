## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-20 - Accessible Custom Selection States in Compose
**Learning:** Screen readers do not automatically infer selection state from visual changes on interactive components in Jetpack Compose. Modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Always use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure accessible custom selection states work correctly.
