## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-18 - Compose Semantics and Selection States
**Learning:** In Jetpack Compose, when adding interactive components using modifiers like `.clickable`, screen readers do not automatically infer selection state from visual changes. Furthermore, using `clearAndSetSemantics` before `.clickable` overrides the click action for TalkBack.
**Action:** Use explicit semantic properties (`.clearAndSetSemantics { contentDescription = combinedDescription; selected = isSelected }`) on components with visual selection states, placing this modifier strictly after the `.clickable` modifier to ensure both accessibility and interactivity function as intended.
