## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Semantics after Clickable
**Learning:** Modifying Jetpack Compose semantics before `.clickable` overrides talkback's click action.
**Action:** Apply `.clearAndSetSemantics` and set `selected` *after* `.clickable` to preserve default action interactions while overriding descriptive semantics.
