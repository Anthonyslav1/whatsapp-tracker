## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Custom Accessible Selection States
**Learning:** By default, Android TalkBack does not automatically infer selection states from visual changes (e.g., background color changes) in custom components like a date selector strip.
**Action:** When implementing custom accessible selection states on interactive components in Jetpack Compose, use explicit semantic properties (`.clearAndSetSemantics { contentDescription = dynamicDescription ; selected = isSelected }`) placed explicitly *after* the `.clickable` modifier to ensure screen readers announce the state correctly without overriding the click action.
