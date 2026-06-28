## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-20 - Accessible Selection States
**Learning:** Screen readers do not automatically infer selection from visual changes. Modifying semantics with `clearAndSetSemantics` before `.clickable` can override the click action for TalkBack.
**Action:** Always place `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly after the `.clickable` modifier to ensure both correct semantics and click actions.
