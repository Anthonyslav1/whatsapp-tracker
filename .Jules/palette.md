## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Custom Selection States
**Learning:** Screen readers do not automatically infer selection from visual changes. When implementing custom accessible selection states on interactive components, we need to explicitly provide a content description and selected state. Modifying semantics before `.clickable` can override the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = "Day Date"; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack reads it correctly without breaking click actions.
