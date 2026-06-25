## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2025-02-25 - Custom Selection States in Compose
**Learning:** Screen readers don't automatically infer selection from visual color changes on interactive components in Jetpack Compose. Modifying semantics before `.clickable` overrides TalkBack click actions.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to properly announce selection state and custom descriptions without breaking tap interactions.
