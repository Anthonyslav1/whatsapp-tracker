## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection states from visual changes on custom interactive components. Furthermore, placing `.clearAndSetSemantics` before `.clickable` can override TalkBack's click action.
**Action:** When creating custom selectable elements, explicitly add `.clearAndSetSemantics { contentDescription = description; selected = isSelected }` after the `.clickable` modifier.
