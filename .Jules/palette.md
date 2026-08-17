## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Explicit Selection State Semantics
**Learning:** Screen readers do not automatically deduce "selected" state from visual background changes, so interactive elements built with `Box`/`.clickable` need explicit `selected` semantic properties.
**Action:** Use `.clearAndSetSemantics { selected = isSelected }` *after* the `.clickable` modifier to explicitly communicate state without overriding the click action.
