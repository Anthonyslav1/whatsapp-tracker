## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-25 - Accessible Date Selectors
**Learning:** TalkBack does not automatically infer selection state from visual background color changes on custom date picker items, and reads separated date components disjointedly.
**Action:** When creating custom selection strips, use `.clearAndSetSemantics` after `.clickable` to assign `selected = isSelected` and combine date fields into a natural spoken phrase like "Monday, October 25, Today".
