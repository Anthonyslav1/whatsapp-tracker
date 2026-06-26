## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-26 - Interactive Component Selection States
**Learning:** Screen readers do not automatically infer selection from visual changes in interactive components. Modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** When implementing custom accessible selection states in Jetpack Compose, use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` placed explicitly *after* the `.clickable` modifier. Also explicitly add `import androidx.compose.ui.semantics.selected`.
