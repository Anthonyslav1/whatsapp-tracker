## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual color changes on custom components like DateSelectorStrip.
**Action:** When implementing custom interactive components, explicitly set `.clearAndSetSemantics { selected = isSelected }` AFTER the `.clickable` modifier so TalkBack correctly announces the selection state without overriding the click action.
