## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-05-10 - DateSelectorStrip Compose Accessibility
**Learning:** TalkBack reads each text element inside DateSelectorStrip individually (e.g., 'JAN', '15'), causing a fragmented experience. Additionally, relying purely on `isSelected` without semantics doesn't clearly inform screen readers of the selected state.
**Action:** Use `.semantics(mergeDescendants = true) { contentDescription = "..."; selected = isSelected }` on the selector item's modifier to combine the text logically and explicitly define its selected state for screen readers.
