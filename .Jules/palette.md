## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-20 - Custom Selection States in Compose
**Learning:** Screen readers do not automatically infer selection state from visual background changes on interactive components (like custom date pickers). Modifying semantics before `.clickable` overrides the TalkBack click action.
**Action:** Implement explicit semantic properties (`.clearAndSetSemantics { contentDescription = desc; selected = isSelected }`) on interactive components, strictly placed *after* the `.clickable` modifier to ensure proper TalkBack interaction and announcement.
