## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-07-22 - Explicit Selection State for Screen Readers
**Learning:** Screen readers do not automatically infer selection from visual changes. When implementing custom accessible selection states on interactive components in Jetpack Compose, the `selected` property must be explicitly set in semantics, and placed *after* the `.clickable` modifier to prevent overriding the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = "${date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())} ${date.dayOfMonth}"; selected = isSelected }` explicitly after `.clickable`.
