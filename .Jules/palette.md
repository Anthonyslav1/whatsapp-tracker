## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-07-18 - Accessible Selection States in Compose
**Learning:** Screen readers like Android TalkBack do not automatically infer selection from visual changes in custom UI components.
**Action:** Use `.clearAndSetSemantics { contentDescription = "$fullDayName ${date.dayOfMonth}"; selected = isSelected }` explicitly *after* the `.clickable` modifier when building custom interactive components.
