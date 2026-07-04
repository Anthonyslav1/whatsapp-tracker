## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics after Clickable for Custom Selection States
**Learning:** When implementing custom selection states using `clearAndSetSemantics` on interactive components (like a custom Box acting as a selectable date), modifying semantics *before* the `.clickable` modifier can override the click action for TalkBack. Screen readers also do not automatically infer selection from visual changes.
**Action:** Always append `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` strictly *after* the `.clickable` modifier, and explicitly add `import androidx.compose.ui.semantics.selected`.
