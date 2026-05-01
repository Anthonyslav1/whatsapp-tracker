## 2024-05-01 - Consolidate Compose Row Semantics
**Learning:** By default, Android TalkBack reads each text element inside a Compose row separately, which is verbose and annoying for dense data rows.
**Action:** Use `.clearAndSetSemantics { contentDescription = "..." }` on the parent `Row` modifier to combine the data into a single, cohesive spoken phrase for screen readers.

## 2024-05-01 - Provide Semantic Summaries for Visual Data Representations
**Learning:** Purely visual elements like Compose `Canvas` are skipped by TalkBack, meaning users miss critical context like weekly trends and charts.
**Action:** Always provide a text-based, descriptive summary using `clearAndSetSemantics` on the parent container (like `Card`) to replace the visual representation with an accessible, sequential breakdown of the chart's data.
