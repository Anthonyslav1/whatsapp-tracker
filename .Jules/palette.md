## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Custom Component Selection Semantics
**Learning:** When building custom interactive components with accessible selection states, TalkBack does not automatically infer selection from visual changes. Also, modifying semantics *before* the `.clickable` modifier can override the click action for TalkBack.
**Action:** Explicitly set `selected = isSelected` inside a `.clearAndSetSemantics` block explicitly *after* the `.clickable` modifier on custom selectable components.
