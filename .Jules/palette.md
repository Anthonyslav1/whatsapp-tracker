## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Compose Interactive Semantics Priority
**Learning:** Screen readers do not automatically infer selection states from custom visual changes. Additionally, modifying semantics *before* the `.clickable` modifier can override TalkBack's click action handling.
**Action:** Always add `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* `.clickable` to ensure the selection state is announced without breaking interaction.
