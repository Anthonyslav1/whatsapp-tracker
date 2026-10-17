## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-10-17 - Compose custom clickable elements accessibility
**Learning:** Screen readers do not automatically infer selection from visual changes in custom Compose elements, and modifying semantics before `.clickable` can override the click action for TalkBack. TalkBack announces default interaction instructions for components with a `.clickable` modifier automatically.
**Action:** When implementing custom accessible selection states on interactive components, use explicit semantic properties (`.clearAndSetSemantics { contentDescription = description; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier. Do not append literal interaction instructions to the `contentDescription` of clickable elements.
