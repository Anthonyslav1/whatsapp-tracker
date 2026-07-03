## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Interactive Component Selection Semantics
**Learning:** Screen readers do not automatically infer selection state from visual changes in Jetpack Compose interactive components (like date pickers). Furthermore, modifying semantics before the `.clickable` modifier can override the click action for TalkBack.
**Action:** When implementing custom accessible selection states, explicitly calculate the `contentDescription` outside the semantics block and use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` strictly placed *after* the `.clickable` modifier. Always include `import androidx.compose.ui.semantics.selected`.
