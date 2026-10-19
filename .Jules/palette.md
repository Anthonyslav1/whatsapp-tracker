## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-11-20 - Explicit Semantics for Custom Selected States
**Learning:** Screen readers (TalkBack) do not automatically announce "selected" based on visual background changes in Jetpack Compose custom components.
**Action:** Explicitly set `selected = isSelected` inside a `.clearAndSetSemantics` block (placed *after* `.clickable`) on custom selectable components so users hear the state change.
