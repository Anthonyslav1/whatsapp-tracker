## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-18 - Full Date Content Descriptions
**Learning:** By default, date strips read out short day names and numbers separately, making it hard for screen reader users to understand the context.
**Action:** Compute a full date string (e.g. "Monday, May 20") and set it as `contentDescription` on the parent container using `clearAndSetSemantics`, along with the `selected` property.
