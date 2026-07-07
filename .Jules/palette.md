## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Accessibility semantics for selectable items
**Learning:** Screen readers do not automatically infer selection states from visual changes. Also, modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** When implementing custom accessible selection states in Jetpack Compose, explicitly set `selected` inside a `clearAndSetSemantics` block placed *after* the `.clickable` modifier. Ensure to add `import androidx.compose.ui.semantics.selected`.
