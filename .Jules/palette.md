## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-10-13 - Accessible Selection States in Compose
**Learning:** By default, modifying visual states like background color for a selected item does not convey the "selected" state to screen readers. Furthermore, adding semantics before the `.clickable` modifier can override the TalkBack click action.
**Action:** Use explicit semantic properties (`.clearAndSetSemantics { contentDescription = "Full text"; selected = true/false }`) *after* the `.clickable` modifier on interactive components to accurately announce the state without breaking the default interaction.
