## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-09-01 - Accessible Date Selector Strip
**Learning:** In Jetpack Compose, screen readers do not automatically infer selected states from visual changes. When combining text inside a selectable component (like a date box), using `.clearAndSetSemantics` *after* `.clickable` is required to communicate the `selected` state and provide a cohesive `contentDescription` without overriding the click action.
**Action:** Always place `.clearAndSetSemantics { selected = isSelected; contentDescription = combinedText }` explicitly after `.clickable` when implementing custom selectable components.
