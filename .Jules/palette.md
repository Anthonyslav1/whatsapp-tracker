## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Explicitly Declare Selection State in Compose
**Learning:** In Jetpack Compose, when you build a custom selectable component (like a date pill) using a simple `Box` and `clickable`, screen readers won't automatically announce it as "Selected". Relying purely on visual color changes (e.g., `CyanAccent` vs `DarkSurfaceVariant`) leaves TalkBack users without context. Furthermore, calling `clearAndSetSemantics` *before* `.clickable` overrides the click action.
**Action:** Use `.clearAndSetSemantics { contentDescription = "Monday, May 1"; selected = isSelected }` explicitly *after* the `.clickable` modifier to ensure screen readers correctly interpret the element's selection state and interaction capabilities.
