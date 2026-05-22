## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2026-05-22 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection from visual state changes in custom components. Using `.clearAndSetSemantics` with `selected` overrides before `.clickable` can break interactions.
**Action:** When creating custom selectable components, apply `.clearAndSetSemantics { selected = isSelected; contentDescription = "..." }` explicitly *after* the `.clickable` modifier to ensure TalkBack accurately announces the selection state and combined text without overriding the click action.
