## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-10 - Date Picker Accessibility Semantics
**Learning:** For custom date pickers, visual elements like abbreviations (e.g., "MON 14") are poorly understood by screen readers when read individually or without context, and selection states on custom `Box` components aren't automatically announced.
**Action:** Use `.clearAndSetSemantics` on the clickable container to combine the full date into a single description (e.g., "Monday, May 14") and explicitly set `selected = isSelected` to properly communicate state.
