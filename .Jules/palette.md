## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-03 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. Modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = "${date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())} ${date.dayOfMonth}"; selected = isSelected }` explicitly *after* the `.clickable` modifier to properly announce selection states and preserve the click action.
