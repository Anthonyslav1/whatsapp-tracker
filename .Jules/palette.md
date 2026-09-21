## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-15 - Explicit Selection Semantics in Compose
**Learning:** When using a custom background or visual state to indicate selection (rather than a native `Selectable`), screen readers like TalkBack do not automatically announce the "selected" state. Furthermore, applying `clearAndSetSemantics` before `clickable` overrides the click action.
**Action:** Apply `.clearAndSetSemantics { contentDescription = "[full text]"; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack announces the state and preserves the action.
