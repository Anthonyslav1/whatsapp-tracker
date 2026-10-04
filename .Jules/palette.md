## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Explicit Semantics for Custom Selected States
**Learning:** Screen readers like TalkBack do not automatically infer selected states from visual changes (like background color) on custom interactive elements.
**Action:** When implementing custom selected states on clickable elements in Compose, use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure TalkBack accurately announces the selection state without overriding the click action.
