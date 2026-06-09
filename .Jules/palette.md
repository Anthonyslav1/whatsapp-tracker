## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics in Interactive Compose Components
**Learning:** For Android TalkBack to properly announce the selection state of interactive Compose components (like list items or custom chips), you cannot rely on visual color changes alone. Modifying semantics with `selected = isSelected` must be explicitly appended *after* the `.clickable` modifier. If it is placed before, it can override the click action or be swallowed.
**Action:** When creating custom selection states in Compose elements, always combine child texts and set selection using `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` chained explicitly *after* the `.clickable` modifier.
