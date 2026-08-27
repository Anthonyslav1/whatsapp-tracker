## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2025-02-27 - Date Selector Accessibility
**Learning:** TalkBack does not automatically infer selection state from visual background changes on custom Compose elements with `.clickable`.
**Action:** Use `.clearAndSetSemantics { contentDescription = combinedText; selected = isSelected }` placed *after* `.clickable` to announce selection status clearly.
