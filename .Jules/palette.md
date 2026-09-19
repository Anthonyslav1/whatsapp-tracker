## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Explicit Selection Semantics for TalkBack
**Learning:** When using visual cues (like background color changes) to indicate a selected state on an interactive `.clickable` element in Jetpack Compose, TalkBack does not automatically infer this state change.
**Action:** Always add an explicit `clearAndSetSemantics` block *after* the `clickable` modifier containing `selected = isSelected` (and usually a combined `contentDescription`) so screen readers accurately announce the element's status.
