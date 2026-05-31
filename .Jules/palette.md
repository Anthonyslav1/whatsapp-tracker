## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2025-02-12 - Explicit Selection Semantics for Compose Components
**Learning:** By default, Android TalkBack doesn't automatically infer selection from visual changes. Also, modifying semantics *before* a `.clickable` modifier can override the action for TalkBack.
**Action:** When implementing custom accessible selection states on interactive components, use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier. Also ensure Composable properties like `stringResource` or `Locale` are resolved outside the lambda since it's not a composable context.
