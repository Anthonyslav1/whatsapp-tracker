## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Custom Selection Semantics
**Learning:** Screen readers do not automatically infer selected states from visual changes. For custom interactive components like date pickers in Compose, TalkBack requires explicitly setting the `selected` property inside a `clearAndSetSemantics` block, placed *after* the `.clickable` modifier to prevent overriding the click action.
**Action:** Always add `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` after `.clickable` on custom interactive components to ensure accessible state communication.
