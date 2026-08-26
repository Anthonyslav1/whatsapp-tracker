## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2025-01-20 - Explicit Accessibility Selection State
**Learning:** Screen readers like TalkBack do not automatically infer a component's selection state from visual changes. Using `.clickable` without explicit semantic selection state leaves users unaware if an item is active.
**Action:** Always append `.clearAndSetSemantics { selected = isSelected; contentDescription = dynamicDescription }` *after* `.clickable` on custom selectable components to announce selection states properly.
