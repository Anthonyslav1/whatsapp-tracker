## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Explicit TalkBack Selection State
**Learning:** TalkBack does not automatically infer selection state from visual color changes on clickable components.
**Action:** Use `.clearAndSetSemantics { contentDescription = desc; selected = isSelected }` placed explicitly after the `.clickable` modifier to communicate selection state to screen readers without overriding the click action.
