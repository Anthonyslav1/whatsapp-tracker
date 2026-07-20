## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-07-20 - Accessible Date Selection
**Learning:** TalkBack screen readers announce visually altered elements (like bold text or color changes) but do not inherently announce their selection state unless it's explicitly stated in semantics. Using abbreviations (like "MON") instead of full names ("Monday") can also be confusing for visually impaired users.
**Action:** Always provide full contextual text and explicitly set the `selected` property inside `clearAndSetSemantics` for interactive date selectors, ensuring the semantics block is placed *after* the `clickable` modifier.
