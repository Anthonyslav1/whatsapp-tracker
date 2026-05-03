## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.
## 2026-05-03 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each element in a Compose row separately, like emoji icons next to text in  or time and ranks in , leading to poor UX for dense data elements.
**Action:** Applied `.clearAndSetSemantics` on the parent `Row` modifiers in `FunFactsCard.kt` and `TopFiveCard.kt` to combine text into clear phrases or hide non-informative emojis.
## 2026-05-03 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each element in a Compose row separately, like emoji icons next to text in FunFactsCard.kt or time and ranks in TopFiveCard.kt, leading to poor UX for dense data elements.
**Action:** Applied .clearAndSetSemantics on the parent Row modifiers in FunFactsCard.kt and TopFiveCard.kt to combine text into clear phrases or hide non-informative emojis.
