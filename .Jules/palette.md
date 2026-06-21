## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Interactive Accessibility Selection States
**Learning:** TalkBack won't automatically announce selection states correctly if custom interactive elements use standard visual changes. Using clearAndSetSemantics before .clickable overrides the action.
**Action:** Use .clearAndSetSemantics { contentDescription = "..."; selected = isSelected } placed explicitly *after* the .clickable modifier for TalkBack to read selection state without breaking click actions.
