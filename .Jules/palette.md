## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Custom Accessible Selection States
**Learning:** When implementing custom selection visual states on interactive components (like custom tabs or date selectors), screen readers don't automatically infer the selection state.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly *after* the `.clickable` modifier to properly announce the selection state without overriding the click action.
