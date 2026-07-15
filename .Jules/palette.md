## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-07-15 - DateSelectorStrip Accessibility
**Learning:** The date selector in `DateSelectorStrip` reads aloud shortened weekday names and numbers (e.g., "WED, 22"), missing context of what was actually selected, and lacking standard accessibility properties.
**Action:** Added `.clearAndSetSemantics` to the clickable container to read the full day and month name, indication if it's "Today", and added the `selected` property to indicate active state for screen readers.
