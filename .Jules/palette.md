## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-08 - Modifier Ordering with Semantics
**Learning:** In Jetpack Compose, the ordering of modifiers is critical for accessibility. If you add `.clearAndSetSemantics` before a `.clickable` modifier on a component, it may strip the click action semantics, making the element unreachable or un-actionable for TalkBack users.
**Action:** Always place `.clearAndSetSemantics` *after* the `.clickable` modifier to ensure the custom description is applied without destroying the built-in click semantics.
