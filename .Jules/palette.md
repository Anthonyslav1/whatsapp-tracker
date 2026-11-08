## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Explicit Semantics for Custom Selected State
**Learning:** In Jetpack Compose, when you build a custom selectable component using a `Box` and a `.clickable` modifier without using `Modifier.selectable`, TalkBack doesn't announce the "Selected" or "Not Selected" state. Modifying semantics before `.clickable` overrides TalkBack's click actions.
**Action:** When implementing custom selection states, always explicitly apply `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` **after** the `.clickable` modifier to ensure screen readers announce the state correctly without breaking click actions.
