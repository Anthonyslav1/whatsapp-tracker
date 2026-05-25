## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Custom Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose, and modifying semantics *before* the `.clickable` modifier can inadvertently override the click action for TalkBack.
**Action:** When implementing custom accessible selection states, use `.clearAndSetSemantics { ... selected = isSelected }` and ensure it is placed explicitly *after* the `.clickable` modifier. Additionally, since the `clearAndSetSemantics` lambda is not a Composable context, compute Composable values (like string formatting or resources) beforehand and store them in variables to use within the semantics block.
