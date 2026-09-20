## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-08 - Accessible Selection States in Compose
**Learning:** Screen readers like TalkBack do not automatically infer selection from visual changes. When implementing custom accessible selection states on interactive components in Jetpack Compose, explicit semantic properties like `selected = isSelected` must be set, and these must be placed explicitly after the `.clickable` modifier to ensure they are interpreted correctly without overriding the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier when creating custom date selectors or tabs.
