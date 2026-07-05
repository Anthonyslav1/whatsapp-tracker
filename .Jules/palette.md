## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection states from visual changes. Modifying semantics before `.clickable` can override click actions.
**Action:** Use `.clearAndSetSemantics { contentDescription = "$dayOfWeek, $month $dayOfMonth"; selected = isSelected }` explicitly *after* the `.clickable` modifier on interactive elements to correctly communicate selection without breaking click behavior.
