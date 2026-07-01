## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-20 - Custom Compose Selection States
**Learning:** Screen readers do not automatically infer selection states from visual changes (like background color) in custom interactive Compose elements (e.g., date selector strips). Modifying semantics before `.clickable` overrides the click action for TalkBack.
**Action:** When implementing custom accessible selection states, explicitly place `.clearAndSetSemantics { contentDescription = "[Full Descriptive Text]"; selected = isSelected }` *after* the `.clickable` modifier to ensure accessibility.
