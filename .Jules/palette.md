## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-11-12 - Explicit Semantics for Custom Selectors in Compose
**Learning:** In Jetpack Compose, TalkBack reads custom components with fragmented texts as separate or meaningless elements and doesn't infer selection state from visual modifiers. Modifying semantics before `.clickable` overrides the click action.
**Action:** Always combine child text elements into a cohesive phrase (e.g., "Monday, October 12" instead of "MON 12") and use `.clearAndSetSemantics` explicitly *after* the `.clickable` modifier on the parent component to set `contentDescription` and `selected` properties correctly.
