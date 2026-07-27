## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2026-07-27 - Consolidated Semantics for Interactive Banners
**Learning:** When using a clickable Card for banners, TalkBack fragments the reading by reading each internal Text element individually, which is a poor UX for a single interactive target.
**Action:** Use `.clearAndSetSemantics { contentDescription = "$title. $subtitle" }` on the parent `Card` explicitly *after* the `.clickable` modifier to unify the announcement while preserving the interactive role.
