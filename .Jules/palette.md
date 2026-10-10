## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-24 - Screen Reader Semantics on Interactive Elements
**Learning:** Screen readers do not automatically infer selection from visual changes in Jetpack Compose, and modifying semantics *before* the `.clickable` modifier can inadvertently override the click action for TalkBack.
**Action:** Always place custom semantic properties (like `contentDescription` and `selected` via `.clearAndSetSemantics`) explicitly *after* the `.clickable` modifier on interactive components, and ensure dynamic descriptions combine relevant text into a cohesive phrase.
