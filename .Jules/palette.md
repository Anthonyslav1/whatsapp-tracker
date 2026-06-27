## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in Compose interactable elements.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier to ensure TalkBack accurately announces the selection state without overriding click actions.
