## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-05-24 - Provide Semantic Selection States
**Learning:** TalkBack screen readers cannot automatically infer the selected state of custom UI elements built with generic `Box` and `Modifier.clickable`. If selection state is only conveyed via background color changes, visually impaired users will not know what is selected.
**Action:** Use `.clearAndSetSemantics { selected = isSelected; contentDescription = dynamicDescription }` on custom selectable components explicitly AFTER the `.clickable` modifier to announce selection state correctly without overriding the native click action.
