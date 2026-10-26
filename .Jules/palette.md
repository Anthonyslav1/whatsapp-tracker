## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-26 - Custom Selection State Semantics
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactable components.
**Action:** When implementing custom accessible selection states on interactive components in Jetpack Compose, use explicit semantic properties (e.g., `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) placed explicitly after the `.clickable` modifier to prevent TalkBack overrides.
