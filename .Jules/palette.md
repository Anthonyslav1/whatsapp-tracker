## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-30 - Date Selector Accessibility Semantics
**Learning:** In Jetpack Compose, applying `.clearAndSetSemantics` to an interactive component (like a date selector box) must be placed explicitly *after* the `.clickable` modifier. If it is placed before, it can override the click action for TalkBack, rendering the element un-clickable for screen reader users. Screen readers also do not automatically infer selection from visual changes, requiring explicit `selected = isSelected` semantic property.
**Action:** Always apply explicit selection semantics (`selected = isSelected`) and composite `contentDescription` on interactive custom components, ensuring `.clearAndSetSemantics` follows `.clickable` in the modifier chain to preserve TalkBack interaction.
