## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Add selected semantics after clickable in custom selection components
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = desc; selected = isSelected }` explicitly *after* the `.clickable` modifier for accessible selection states.
