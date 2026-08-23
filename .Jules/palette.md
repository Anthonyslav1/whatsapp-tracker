## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Explicit Selection State Semantics
**Learning:** Screen readers do not automatically infer selection from visual changes in custom Compose components. Modifying semantics before .clickable can override the click action for TalkBack.
**Action:** Use explicit semantic properties like .clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected } placed explicitly after the .clickable modifier to ensure custom accessible selection states work correctly with TalkBack.
