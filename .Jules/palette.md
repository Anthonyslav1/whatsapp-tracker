## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-06-25 - Talkback Support on Selectable Elements
**Learning:** Screen readers do not automatically infer selected states from visual background color changes on interactive components.
**Action:** When creating a selectable UI element with `.clickable`, use `.clearAndSetSemantics { selected = isSelected; contentDescription = "..." }` placed exactly after the `.clickable` modifier to explicitly communicate state without overriding the action.
