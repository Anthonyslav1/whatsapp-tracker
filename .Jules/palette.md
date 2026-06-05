## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-05 - Accessible Selection States in Compose
**Learning:** TalkBack does not automatically infer selection state from visual changes (e.g. background color). When implementing custom selection states on interactive elements using `clickable`, the `selected` semantics must be explicitly set. Additionally, if you modify semantics *before* `clickable`, it can override the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` and ensure it is placed explicitly *after* the `.clickable` modifier in the modifier chain to properly announce selection state without breaking interactivity.
