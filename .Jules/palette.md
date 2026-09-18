## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Explicit Accessible Selection States
**Learning:** In Jetpack Compose, modifying background colors or UI elements to indicate selection state is not automatically announced by screen readers if using `Modifier.clickable` without explicit roles.
**Action:** When implementing custom interactive elements, append `.clearAndSetSemantics { contentDescription = description; selected = isSelected }` explicitly *after* `.clickable` to ensure TalkBack properly reads the component's state without overriding the native click action.
