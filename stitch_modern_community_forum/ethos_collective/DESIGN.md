# Design System: The Editorial Nexus

## 1. Overview & Creative North Star
**Creative North Star: "The Curated Sanctuary"**

This design system rejects the cluttered, high-density noise of traditional forums in favor of a "Curated Sanctuary." We are merging the community-driven intelligence of Reddit with the architectural clarity of Notion and the editorial elegance of Medium. 

The goal is to move away from "software" and toward "experience." We achieve this through **Intentional Asymmetry** (using whitespace as a functional element), **Tonal Depth** (replacing lines with light), and **Typographic Authority**. Every screen should feel like a well-composed magazine spread—spacious, calm, and intellectually inviting.

---

## 2. Colors & Surface Philosophy
The palette is rooted in high-luminance neutrals and sophisticated indigos. We do not use color to "decorate"; we use it to "direct."

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders for sectioning. 
Structure must be defined through:
*   **Background Shifts:** Transitioning from `surface` to `surface_container_low`.
*   **Tonal Transitions:** Using `surface_container_lowest` for interactive cards against a `surface_container` background.

### Surface Hierarchy & Nesting
Treat the UI as a series of nested, organic layers. 
*   **Level 0 (Base):** `surface` (#f8f9fa) – The infinite canvas.
*   **Level 1 (Sections):** `surface_container_low` (#f1f4f5) – Defines broad content areas (e.g., sidebar background).
*   **Level 2 (Interactive/Cards):** `surface_container_lowest` (#ffffff) – The highest "white" for post cards and input areas.
*   **Level 3 (Floating/Overlays):** `surface_bright` (#f8f9fa) with Glassmorphism.

### The "Glass & Gradient" Rule
To elevate CTAs beyond a "template" look, use subtle linear gradients for `primary` elements, transitioning from `primary` (#2b51d9) to `primary_container` (#4c6ef5) at a 135° angle. For floating navigation or headers, use `surface_container_lowest` at 80% opacity with a `24px` backdrop-blur to create a "frosted glass" effect that keeps the community content visible but softened.

---

## 3. Typography
We utilize a dual-typeface system to balance personality with readability.

*   **The Display Face:** `Plus Jakarta Sans`. Used for `display`, `headline`, and `title` tokens. It provides a geometric, modern warmth that feels premium and friendly.
*   **The Reading Face:** `Inter`. Used for `body` and `label` tokens. It is engineered for legibility in long-form discussions.

**Hierarchy Strategy:**
*   **Headline-LG (2rem):** Use for thread titles. High-contrast against `body-md` to ensure the content's "voice" is heard immediately.
*   **Body-LG (1rem):** The standard for forum posts. Increased line-height (1.6) is mandatory to mimic the Medium reading experience.
*   **Label-MD (0.75rem):** Used for metadata (timestamps, category tags). Use `on_surface_variant` (#5a6062) to keep this information secondary to the conversation.

---

## 4. Elevation & Depth
Depth is created through light physics, not structural boxes.

*   **The Layering Principle:** A post card (`surface_container_lowest`) sitting on the main feed (`surface`) provides a natural "lift" due to the contrast in brightness. 
*   **Ambient Shadows:** For elements that require true elevation (e.g., a "Create Post" fab or a dropdown), use an ultra-diffused shadow:
    *   `X: 0, Y: 8, Blur: 32, Spread: -4`
    *   Color: `on_surface` (#2d3335) at **6% opacity**. 
*   **The "Ghost Border" Fallback:** If a border is required for accessibility (e.g., in a high-density data table), use `outline_variant` at **15% opacity**. It should be felt, not seen.
*   **Rounding Scale:** 
    *   **Cards/Containers:** `lg` (2rem) – This large radius is the signature of the system, creating a friendly, approachable vibe.
    *   **Buttons/Inputs:** `full` (9999px) – Provides a distinct tactile contrast to the larger cards.

---

## 5. Components

### Cards (The Core Component)
*   **Rules:** No dividers. Use `32px` padding (`xl` spacing) internally. 
*   **Interaction:** On hover, a card should shift from `surface_container_lowest` to a subtle `surface_bright` with an ambient shadow.

### Buttons
*   **Primary:** Gradient of `primary` to `primary_container`. White text (`on_primary`). Roundedness: `full`.
*   **Secondary:** `surface_container_high` background with `primary` text. No border.
*   **Tertiary:** Ghost style. `on_surface` text. Only a background shift to `surface_variant` on hover.

### Input Fields
*   **Style:** `surface_container_lowest` background. 
*   **State:** On focus, the "Ghost Border" becomes 100% opacity `primary`, and the background stays white. This creates a "glow" effect without heavy shadows.

### Chips (Categories/Tags)
*   **Style:** `secondary_container` (#dde1ff) background with `on_secondary_container` (#2a48b2) text.
*   **Shape:** `full` (pill-shaped). Use for thread tags like "Question," "Discussion," or "Announcement."

### Threaded Lists (Comments)
*   **Rules:** Forbid vertical lines for comment threading. Instead, use increasing `left-margin` (e.g., 24px per level) and subtle background shifts (e.g., parent is `surface`, child is `surface_container_low`).

---

## 6. Do's and Don'ts

### Do
*   **Do** use generous white space. If a layout feels "full," add 16px of padding.
*   **Do** use "Plus Jakarta Sans" for all interactive elements (buttons, nav items) to maintain a modern brand voice.
*   **Do** use `surface_container_highest` for "sticky" or "pinned" content to denote weight.

### Don't
*   **Don't** use `#000000` for text. Always use `on_surface` (#2d3335) to maintain the "Soft Modern" feel.
*   **Don't** use standard `1px` dividers to separate comments; let the "Tonal Layering" do the work.
*   **Don't** use square corners. Even the smallest element (like a checkbox) should have at least a `sm` (0.5rem) radius.
*   **Don't** use high-saturation reds for errors. Use the sophisticated `error` (#a8364b) for a more editorial, less alarming tone.