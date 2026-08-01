## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-01 - Explicit Semantic Selection in Compose
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose, and modifying semantics before the `.clickable` modifier can override the click action for TalkBack.
**Action:** Always implement custom accessible selection states using explicit semantic properties (like `selected`) and place `.clearAndSetSemantics` explicitly after the `.clickable` modifier.
