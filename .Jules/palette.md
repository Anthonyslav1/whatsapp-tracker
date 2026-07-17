## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Compose Custom Selectable Items Accessibility
**Learning:** Screen readers (like TalkBack) do not automatically infer the selected state of an item from visual background color changes in Compose.
**Action:** When creating custom selectable interactive components in Jetpack Compose, explicitly set the `selected` property inside a `.clearAndSetSemantics` block. Ensure this block is placed *after* the `.clickable` modifier, otherwise modifying semantics before `.clickable` can inadvertently override or break the click action for TalkBack users.
