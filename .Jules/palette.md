## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Accessible Selection States in Jetpack Compose
**Learning:** By default, Screen readers do not automatically infer selection from visual changes in custom clickable components in Jetpack Compose.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier on interactive components, so that TalkBack can accurately announce the selection state without overriding the click action.
