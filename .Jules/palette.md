## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-06-25 - Jetpack Compose Selection Semantics Modifier Ordering
**Learning:** For Jetpack Compose, when overriding semantics to signify `selected = true` using `clearAndSetSemantics`, the modifier must be placed *after* the `.clickable` modifier. Otherwise, it may completely overwrite the click action node causing TalkBack or interactions to fail. Also, standard screen readers do not automatically infer selection state from visual/background color changes alone, requiring explicit semantics mapping.
**Action:** Always append `.clearAndSetSemantics { selected = isSelected }` sequentially *after* interactive `.clickable` modifiers on customized selectable components.
