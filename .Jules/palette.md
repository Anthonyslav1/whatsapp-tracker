## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-01 - Explicit Accessible Selection States in Compose
**Learning:** Screen readers in Jetpack Compose do not automatically infer selection state from visual changes (like background color). If semantics are modified *before* `.clickable`, it can override the click action for TalkBack.
**Action:** Explicitly set semantic properties (`selected = isSelected`, `contentDescription = accessibilityDesc`) using `.clearAndSetSemantics` *after* the `.clickable` modifier to ensure TalkBack announces the state correctly without breaking interaction.
