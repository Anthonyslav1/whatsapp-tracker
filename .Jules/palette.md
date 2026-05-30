## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-18 - Jetpack Compose Selected State Semantics
**Learning:** By default, Android TalkBack doesn't automatically infer selection from visual state changes. Also, applying `.clearAndSetSemantics` *before* `.clickable` will completely erase the click handler's implicit semantic action.
**Action:** When implementing custom accessible selection states, explicitly use `.clearAndSetSemantics { contentDescription = "..."; selected = isSelected }` and ensure this modifier is placed directly *after* `.clickable` so TalkBack reads both the custom combined text and the standard interactive "Double-tap to toggle" selection hints properly.
