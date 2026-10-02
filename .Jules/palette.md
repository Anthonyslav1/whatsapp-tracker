## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2024-10-02 - Ensure explicit TalkBack Select State
**Learning:** For interactive composables, screen readers do not automatically infer selection from visual styling changes.
**Action:** Use `.clearAndSetSemantics` explicitly after `.clickable` to assign `selected = isSelected` and combine text elements to prevent fragmented TalkBack readings.
