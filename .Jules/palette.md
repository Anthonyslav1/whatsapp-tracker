## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessible Selection States on Clickable Elements
**Learning:** Screen readers (like TalkBack) do not automatically infer selection state from visual changes (e.g., background color). When building custom interactive components with Jetpack Compose, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Always place custom semantics `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure the component is read correctly without breaking its interactivity.
