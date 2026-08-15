## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Ensure Accessible State for Selectable Items
**Learning:** By default, Android TalkBack doesn't read the selected state for selectable items based purely on visual changes (like background color).
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` to explicitly announce selection states for screen readers.
