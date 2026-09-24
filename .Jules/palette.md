## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Ensure TalkBack selection state in custom visual lists
**Learning:** TalkBack does not automatically infer selection state from visual changes (e.g. background color or text color) in custom lists. If `.clickable` is used, its default semantics override any custom semantics applied *before* it, preventing TalkBack from correctly reading the selection state.
**Action:** When implementing custom accessible selection states, explicitly apply semantic properties (e.g., `.clearAndSetSemantics { contentDescription = description; selected = isSelected }`) *after* the `.clickable` modifier to ensure screen readers announce the selection properly.
