## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-07-28 - Explicit Selection State Semantics
**Learning:** Screen readers do not automatically infer selection from visual changes in custom Compose interactive elements, and overriding semantics before `.clickable` can disable the click action for TalkBack.
**Action:** Implement explicit accessible selection states using `.clearAndSetSemantics { contentDescription = description; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack properly reads the state without breaking the interaction.
