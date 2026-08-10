## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-08-10 - TalkBack Overrides with Clickable Modifiers
**Learning:** When configuring custom accessible selection states in Jetpack Compose, setting semantics before `.clickable` can cause TalkBack to override the intended click action and announce states incorrectly.
**Action:** Always append `.clearAndSetSemantics { contentDescription = fullDateText; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure custom properties are read correctly by TalkBack while preserving interaction.
