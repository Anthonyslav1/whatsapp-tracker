## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Explicit Selection State for Interactive Compose Elements
**Learning:** By default, Android TalkBack doesn't automatically infer selection state from visual changes on interactive components (like background color updates in a list).
**Action:** Explicitly set `selected` in the `clearAndSetSemantics` block *after* the `.clickable` modifier to ensure screen readers correctly announce the state without overriding the interaction.
