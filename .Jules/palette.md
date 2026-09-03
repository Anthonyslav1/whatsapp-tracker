## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-10-24 - Accessibility states on clickable components
**Learning:** In Jetpack Compose, modifying semantics before the `.clickable` modifier can override the click action for TalkBack. Screen readers do not automatically infer selection from visual changes.
**Action:** When implementing custom accessible selection states on interactive components, use explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier.
