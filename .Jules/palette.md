## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-24 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in custom interactive components. Furthermore, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Implement custom accessible selection states using explicit semantic properties (`.clearAndSetSemantics { contentDescription = "Full Description"; selected = isSelected }`) placed strictly *after* the `.clickable` modifier to ensure TalkBack accurately announces both the content and its selected state without breaking interactivity.
