## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-07-19 - Jetpack Compose Semantics Selection
**Learning:** Screen readers do not automatically infer selection from visual changes in interactive components.
**Action:** Use `.clearAndSetSemantics { contentDescription = "$fullDayName, $monthName ${date.dayOfMonth}"; selected = isSelected }` placed explicitly *after* the `.clickable` modifier to ensure TalkBack reads the state correctly.
