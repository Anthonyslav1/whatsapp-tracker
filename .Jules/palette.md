## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-06-01 - Compose Modifier Ordering for Semantics
**Learning:** In Jetpack Compose, when adding custom accessible selection states using `.clearAndSetSemantics`, you must place it *after* the `.clickable` modifier. Modifying semantics before `.clickable` can override the click action for TalkBack, making the element inaccessible.
**Action:** Always place `.clearAndSetSemantics { selected = isSelected }` after `.clickable` on interactive components to ensure screen readers correctly announce the selected state without breaking interactivity.
