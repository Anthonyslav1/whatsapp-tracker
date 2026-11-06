## 2024-05-18 - Accessible Selection States in Compose
**Learning:** Screen readers do not automatically infer selection states from visual background color changes in custom Compose elements (like a date selector).
**Action:** Use `.clearAndSetSemantics { contentDescription = dynamicDescription; selected = isSelected }` explicitly after the `.clickable` modifier to ensure TalkBack announces the selection state correctly without overriding the click action.
