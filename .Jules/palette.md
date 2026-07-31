## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Explicit Selection Semantics in Date Strips
**Learning:** Screen readers do not automatically infer selected states from visual changes. Custom interactive components in Compose must explicitly expose their selection state using `selected = true/false` in their semantics. Additionally, semantics must be placed after `.clickable` to avoid overriding the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = combinedText; selected = isSelected }` on custom selectable components after the `.clickable` modifier.
