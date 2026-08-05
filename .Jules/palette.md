## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-18 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selected state from visual background changes, and overriding semantics before a `.clickable` modifier breaks the click action for TalkBack.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicText; selected = isSelected }` explicitly *after* the `.clickable` modifier to provide accessible selection state without destroying interactivity.
