## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-01 - Explicit Selection Semantics for Custom Components
**Learning:** Screen readers do not automatically infer selection state from visual changes (e.g., background color) in custom interactive components. Furthermore, modifying semantics before the `.clickable` modifier can override the click action for TalkBack.
**Action:** When implementing custom selection states in Jetpack Compose, explicitly use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` and place it explicitly *after* the `.clickable` modifier.
