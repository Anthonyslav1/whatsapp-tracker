## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Date Selector Semantics
**Learning:** In interactive custom components like a date selector, visual indicators of selection are not automatically picked up by screen readers. Furthermore, splitting dates into separate text elements (day name vs day number) makes screen reading disjointed.
**Action:** Use `.clearAndSetSemantics` to provide a full, cohesive date string (e.g., "Monday, 15") and explicitly set `selected = isSelected` so the screen reader announces the selection state properly.
