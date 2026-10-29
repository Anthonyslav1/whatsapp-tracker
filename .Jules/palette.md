## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-02 - Custom Accessible Selection States
**Learning:** TalkBack does not automatically infer selection state from visual background changes. Modifying semantics before `.clickable` can override the TalkBack click action.
**Action:** When implementing custom interactive elements like a date picker, use explicit semantic properties (`selected = isSelected`) placed explicitly *after* the `.clickable` modifier. Combine the text into `contentDescription`.
