## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2025-02-17 - Semantics for Selectable UI Elements in Jetpack Compose
**Learning:** TalkBack does not automatically announce the selected state for custom Box or Row components acting as selectors, even if colors change visually. Furthermore, if `clearAndSetSemantics` is used before the `clickable` modifier, it overrides the click action and breaks the button's interactivity for screen readers.
**Action:** When implementing custom selection states, always use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure screen readers announce selection correctly without breaking the click interaction.
