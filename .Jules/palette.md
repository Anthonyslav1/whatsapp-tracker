## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Interactive Component Selection Semantics
**Learning:** In Jetpack Compose, screen readers do not automatically infer selection states from visual changes. Additionally, modifying semantics before `.clickable` overrides TalkBack click actions.
**Action:** Always append `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier for accessible selection states.
