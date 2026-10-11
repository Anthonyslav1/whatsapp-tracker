## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-10-10 - Explicit Selection State for Custom Components
**Learning:** Screen readers do not automatically infer selection state from visual color changes on clickable components in Jetpack Compose.
**Action:** When building custom interactive components (like a DateSelector), explicitly use `.clearAndSetSemantics { selected = isSelected }` after the `.clickable` modifier to convey state to TalkBack.
