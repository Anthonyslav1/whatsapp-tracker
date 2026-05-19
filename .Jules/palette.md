## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-15 - Interactive Component Selection Semantics
**Learning:** Screen readers do not automatically infer selected states from visual differences in custom Compose UI components. Also, applying semantic adjustments before the `.clickable` modifier can inadvertently overwrite or break default accessibility click handling.
**Action:** When creating custom toggleable/selectable components, explicitly set `selected = isSelected` within a `.clearAndSetSemantics` block, and ensure this modifier is placed *after* `.clickable` to maintain correct interaction.
