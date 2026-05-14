## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-05-14 - TalkBack Selected State in Compose
**Learning:** Screen readers like TalkBack do not automatically infer selection state from visual color changes in Jetpack Compose. Furthermore, applying `.clearAndSetSemantics` before `.clickable` can override or break the click action for TalkBack users.
**Action:** When creating custom accessible selection states, explicitly place `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` *after* the `.clickable` modifier to ensure both proper reading of the state and functional clickability.
