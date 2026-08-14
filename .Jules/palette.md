## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-20 - Proper placement of clearAndSetSemantics for selection
**Learning:** Screen readers do not automatically infer selection from visual changes, and modifying semantics before .clickable can override the click action for TalkBack.
**Action:** When implementing custom accessible selection states, explicitly use .clearAndSetSemantics { contentDescription = description; selected = isSelected } after the .clickable modifier.
