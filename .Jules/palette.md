## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Explicit Selection States on Custom Tabs
**Learning:** Screen readers like TalkBack do not automatically announce the selection state of custom tabs or pills (like a date selector) built with `Box` and background colors, even if they visually appear selected.
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` on the item's modifier, placed explicitly *after* the `.clickable` modifier so it doesn't override the click action for TalkBack.
