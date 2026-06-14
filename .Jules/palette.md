## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-06-14 - Interactive Component Selection Semantics
**Learning:** Screen readers do not automatically infer selection state from visual changes in custom Compose components. Furthermore, applying `clearAndSetSemantics` before `.clickable` can override the click action for TalkBack.
**Action:** When creating custom accessible selection states, use `.clearAndSetSemantics { contentDescription = "exact content string"; selected = isSelected }` placed explicitly *after* the `.clickable` modifier. Ensure Composable function calls for properties like `contentDescription` are evaluated outside the lambda.
