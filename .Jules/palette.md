## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Screen Reader Selection State in Compose
**Learning:** Screen readers (like TalkBack) do not automatically infer an item's selection state from custom visual changes (like background color updates). If we don't explicitly set the semantic `selected` property, the user has no way of knowing which item is currently active. Furthermore, setting semantics *before* the `.clickable` modifier can inadvertently override or break the click action for TalkBack.
**Action:** When implementing custom selection states, always append `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier on the interactive component.
