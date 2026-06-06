## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2025-02-12 - Expose Selection State in Custom Compose Components
**Learning:** Screen readers like TalkBack do not automatically infer selection state from visual changes such as background color. When using `clearAndSetSemantics` to consolidate text elements on a custom interactive component, you must also explicitly declare `selected = isSelected`.
**Action:** When creating custom accessible selection states, place `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack properly reads the selection state without overriding the click action.
