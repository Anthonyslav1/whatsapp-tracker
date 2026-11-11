## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Explicit Semantic Selection State
**Learning:** Screen readers do not automatically infer an item's selection status from visual changes. Modifying semantics before a `.clickable` modifier can inadvertently override click actions in TalkBack.
**Action:** Manually set `.clearAndSetSemantics { selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure correct selection announcement without breaking interaction.
