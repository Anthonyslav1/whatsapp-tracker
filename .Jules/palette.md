## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection states from visual changes alone on custom interactive elements (like a Box with a clickable modifier).
**Action:** When implementing custom accessible selection states, use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` and place it explicitly *after* the `.clickable` modifier to ensure TalkBack reads the state correctly without overriding the click action.
