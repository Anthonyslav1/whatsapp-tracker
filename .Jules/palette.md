## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-25 - Explicit Selection Semantics
**Learning:** Screen readers do not automatically infer selection state from visual changes in custom Compose components. Modifying semantics before `.clickable` can override the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = description; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack properly reads the component's state and function.