## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Compose Selected State Semantics
**Learning:** TalkBack won't automatically read selection state for custom interactive UI, and if `clearAndSetSemantics` with a `selected` state is applied *before* the `.clickable` modifier, TalkBack's click actions can be overridden or act weirdly.
**Action:** Always apply custom selected semantics via `.clearAndSetSemantics { selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure accessibility correctly recognizes both the action and the state.
