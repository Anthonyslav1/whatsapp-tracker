## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-02 - Explicit Semantic State for Interactive Components
**Learning:** Screen readers (like Android TalkBack) do not automatically infer selection state from visual changes alone (e.g., changing background color).
**Action:** When implementing custom interactive elements like a selectable date chip, always explicitly set `selected = isSelected` inside `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` *after* the `.clickable` modifier to ensure accurate accessibility readouts without breaking the click action.
