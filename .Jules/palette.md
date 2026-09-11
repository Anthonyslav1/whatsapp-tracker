## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics for Custom Selected States
**Learning:** TalkBack doesn't infer selection states from visual changes. When implementing custom selected states on interactive elements in Compose, the semantics must be set explicitly.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` *after* the `.clickable` modifier to ensure both the click action and the selection state are correctly announced.
