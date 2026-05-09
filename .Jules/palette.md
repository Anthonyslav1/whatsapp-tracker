## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-10 - Customized Accessible Selection States
**Learning:** Using `.semantics(mergeDescendants = true)` in conjunction with `selected = isSelected` ensures that customized, clear content descriptions are announced while preserving TalkBack's native ability to announce selection status.
**Action:** When overriding semantics on selectable components (e.g., date pickers), define `contentDescription` inside the `semantics` block and include `selected = isSelected` to prevent losing the accessibility selection status context.
