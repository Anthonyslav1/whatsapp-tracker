## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Compose Semantics Order for Clickable Elements
**Learning:** When assigning custom semantic states (like `selected`) to a clickable component in Compose, changing semantics before the `.clickable` modifier can inadvertently override TalkBack's click action handling.
**Action:** Always place `.clearAndSetSemantics` or `.semantics` explicitly *after* the `.clickable` modifier. This preserves the default TalkBack click interaction while allowing custom spoken text and semantic states to be accurately conveyed.
