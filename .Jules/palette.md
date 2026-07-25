## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Improve Screen Reader Experience for Date Selector
**Learning:** In Compose, a component like a `Box` used for date selection with inner `Text` items gets read verbosely and unhelpfully by TalkBack (e.g., "WED, 24, double tap to activate"). This lacks context and full semantic clarity.
**Action:** Use `.clearAndSetSemantics` on the clickable parent container to override the default text reading. Provide a fully spelled-out date (e.g., "Wednesday, May 24") in `contentDescription`, and set the `selected` property to ensure the selection state is correctly announced to screen reader users.
