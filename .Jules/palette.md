## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-31 - Accessible Interactive Components
**Learning:** Screen readers do not automatically infer selection from visual changes, and default interactive announcements for components like date pickers can be verbose and lack context.
**Action:** Implement explicit semantic properties (`clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }`) after the `.clickable` modifier to provide concise, combined contextual descriptions and accurate selection states for TalkBack users.
