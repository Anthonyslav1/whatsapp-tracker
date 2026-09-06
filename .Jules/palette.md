## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics Ordering with Clickable
**Learning:** Modifying semantics (like using `.clearAndSetSemantics`) before `.clickable` in a Compose modifier chain can override the click action for TalkBack. Screen readers also do not automatically infer selection state from visual changes.
**Action:** When implementing custom accessible selection states, use explicit semantic properties (like `selected`) and place the semantic modifier explicitly *after* the `.clickable` modifier to ensure both custom readouts and click interactions work as intended.
