## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-24 - Interactive Component Accessible Selection
**Learning:** In Jetpack Compose, modifying semantics to announce selected state (or content description) *before* the `.clickable` modifier can inadvertently override or hide the native click action for screen readers. Furthermore, TalkBack does not automatically infer selection from visual color changes.
**Action:** Always append `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` *after* `.clickable` to preserve interaction functionality while properly indicating selection state.
