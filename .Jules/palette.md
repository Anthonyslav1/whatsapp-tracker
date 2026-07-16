## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Compose Semantics Selection State Ordering
**Learning:** TalkBack does not automatically infer selection from visual changes. Modifying semantics before `.clickable` can override the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly after the `.clickable` modifier on interactive components in Jetpack Compose.
