## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection state from visual changes (like background color). If selection is custom-implemented on interactive components (like Box with clickable), TalkBack won't announce if it's selected.
**Action:** Use `.clearAndSetSemantics { contentDescription = "${fullDayName}, ${dayOfMonth}" ; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack accurately announces selection status and click actions.
