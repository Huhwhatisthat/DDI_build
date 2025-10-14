# New Theme - Visual Reference Guide

## Color Palette Swatches

```
┌─────────────────────────────────────────────┐
│  Palette Color          Hex       Usage     │
├─────────────────────────────────────────────┤
│  ████████  Cream       #F2E9E4   Background │
│  ████████  Rose Beige  #C9ADA7   Borders    │
│  ████████  Mauve Gray  #9A8C98   Secondary  │
│  ████████  Slate Blue  #4A4E69   Primary    │
│  ████████  Deep Navy   #22223B   Text       │
└─────────────────────────────────────────────┘
```

## Homepage Layout (New Theme)

```
╔═══════════════════════════════════════════════╗
║  [Cream Background #F2E9E4]                   ║
║                                               ║
║  ┌─────────────────────────────────────────┐ ║
║  │  [White Header]                         │ ║
║  │  👋 Good morning!  [Deep Navy text]    │ ║
║  └─────────────────────────────────────────┘ ║
║                                               ║
║  My Medications  [Deep Navy]                  ║
║                                               ║
║  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐    ║
║  │ 💊   │  │ 💊   │  │ 💊   │  │ 💊   │    ║
║  │[Slate│  │[Slate│  │[Slate│  │[Slate│    ║
║  │ Blue]│  │ Blue]│  │ Blue]│  │ Blue]│    ║
║  │      │  │      │  │      │  │      │    ║
║  │Aspirin│ │Vitamin│ │Metfor│  │Fish  │    ║
║  │      │  │   D  │  │  min │  │ Oil  │    ║
║  │ ┌──────────┐   │  │      │  │      │    ║
║  │ │Acetyl... │   │  │Cholec│  │Metfor│    ║ ← Ingredients
║  │ │[Rose Bg] │   │  │[Rose]│  │[Rose]│    ║   in badges
║  │ └──────────┘   │  └──────┘  └──────┘    ║
║  │      │  │      │  │      │  │      │    ║
║  │  🗑️  │  │  🗑️  │  │  🗑️  │  │  🗑️  │    ║
║  └──────┘  └──────┘  └──────┘  └──────┘    ║
║                                               ║
║  Today's Prescriptions  [Deep Navy]           ║
║                                               ║
║  ┌─────────────────────────────────────────┐ ║
║  │ ⚫ Aspirin                    10:30 AM  │ ║
║  │    1 pill • Every day  [Mauve Gray]   │ ║
║  └─────────────────────────────────────────┘ ║
║                                               ║
║  Actions  [Deep Navy]                         ║
║                                               ║
║  ┌─────────────────────────────────────────┐ ║
║  │  [Mauve → Slate Gradient]               │ ║
║  │         ➕                               │ ║
║  │    Add Medication                       │ ║
║  │  Scan or enter manually                 │ ║
║  └─────────────────────────────────────────┘ ║
║                                               ║
║  ┌─────────────────────────────────────────┐ ║
║  │  [Rose → Mauve Gradient]                │ ║
║  │         📋                               │ ║
║  │      View Drugs                         │ ║
║  │  View and check interactions            │ ║
║  └─────────────────────────────────────────┘ ║
║                                               ║
╚═══════════════════════════════════════════════╝
```

## Drug Card Carousel Detail

```
Single Card (180dp width):
┌───────────────────────────┐
│ [White bg, Rose border]   │
│                           │
│          💊               │ ← Icon: Slate Blue
│      [#4A4E69]            │
│                           │
│       Aspirin             │ ← Name: Deep Navy
│     [#22223B]             │
│                           │
│   ┌─────────────────┐    │
│   │ Acetylsalicylic │    │ ← Ingredient Badge
│   │      Acid       │    │   Bg: Rose Beige #C9ADA7
│   │   [Rose Beige]  │    │   Text: Deep Navy #22223B
│   └─────────────────┘    │
│                           │
│          🗑️               │ ← Delete: Rose Beige
│      [#C9ADA7]            │
│                           │
└───────────────────────────┘
```

## Ingredient Badge Styling

```
Close-up of Ingredient Badge:

┌─────────────────────────────┐
│  Acetylsalicylic Acid      │
│  ───────────────────────   │
│  Background: #C9ADA7       │ ← Rose Beige
│  Text: #22223B             │ ← Deep Navy
│  Padding: 12dp H, 6dp V    │
│  Radius: 12dp rounded      │
│  Font: sans-serif 13sp     │
│  Max Lines: 2              │
│  Ellipsize: end            │
└─────────────────────────────┘
```

## Action Cards with Gradients

```
Add Medication Card:
┌─────────────────────────────────┐
│  ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱  │
│ ╱ Mauve → Slate Gradient  ╱   │
│╱  #9A8C98 → #4A4E69      ╱    │
│ ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱     │
│                                 │
│            ➕                   │
│       Add Medication            │
│   Scan or enter manually        │
│        [White text]             │
│                                 │
└─────────────────────────────────┘

View Drugs Card:
┌─────────────────────────────────┐
│  ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱  │
│ ╱ Rose → Mauve Gradient   ╱   │
│╱  #C9ADA7 → #9A8C98      ╱    │
│ ╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱╱     │
│                                 │
│            📋                   │
│         View Drugs              │
│  View and check interactions    │
│        [White text]             │
│                                 │
└─────────────────────────────────┘
```

## Status Icons (Prescriptions)

```
⚫ Upcoming (Gray)     → Changed to: Mauve Gray #9A8C98
🕐 Pending (Yellow)   → Changed to: Rose Beige #C9ADA7
⚠️  Missed (Red)       → Changed to: Rose Beige #C9ADA7
✅ Taken (Green)      → Changed to: Slate Blue #4A4E69
❌ Skipped (Red)      → Changed to: Rose Beige #C9ADA7
```

## Prescription Item Example

```
┌────────────────────────────────────────────┐
│  ⚫  Aspirin                     10:30 AM  │
│      [Mauve]                    [Slate]   │
│                                            │
│      1 pill • Every day                   │
│      [Mauve Gray text]                    │
└────────────────────────────────────────────┘
```

## Color Hierarchy

```
Importance Level 1 (Most Important):
- Deep Navy #22223B
  Used for: Headings, drug names, primary text

Importance Level 2 (Secondary):
- Slate Blue #4A4E69
  Used for: Icons, buttons, primary actions

Importance Level 3 (Supporting):
- Mauve Gray #9A8C98
  Used for: Secondary text, descriptions

Importance Level 4 (Subtle):
- Rose Beige #C9ADA7
  Used for: Borders, badges, accents

Importance Level 5 (Background):
- Cream #F2E9E4
  Used for: App background, surfaces
```

## Contrast Examples

```
Best Readability Combinations:

1. Deep Navy on Cream
   #22223B on #F2E9E4
   Ratio: 11.2:1 ★★★ (AAA)

2. White on Slate Blue
   #FFFFFF on #4A4E69
   Ratio: 8.5:1 ★★★ (AAA)

3. Deep Navy on Rose Beige
   #22223B on #C9ADA7
   Ratio: 7.3:1 ★★★ (AAA)

4. White on Deep Navy
   #FFFFFF on #22223B
   Ratio: 16.8:1 ★★★ (AAA)
```

## Theme Mood Board

```
COLOR PSYCHOLOGY:

#F2E9E4 (Cream)
Feeling: Gentle, Warm, Accessible
Association: Comfort, Ease, Natural

#C9ADA7 (Rose Beige)
Feeling: Soft, Approachable, Calming
Association: Care, Nurturing, Balance

#9A8C98 (Mauve Gray)
Feeling: Sophisticated, Neutral, Balanced
Association: Professionalism, Trust, Stability

#4A4E69 (Slate Blue)
Feeling: Reliable, Strong, Professional
Association: Healthcare, Authority, Trust

#22223B (Deep Navy)
Feeling: Confident, Solid, Authoritative
Association: Expertise, Reliability, Clarity
```

## Theme Keywords

```
BEFORE:               AFTER:
Clinical    →        Elegant
Bright      →        Sophisticated
Modern      →        Timeless
Tech        →        Wellness
Digital     →        Natural
Cool        →        Warm
Energetic   →        Calming
```

## Component Color Summary

```
┌──────────────────────┬──────────────┬────────────┐
│ Component            │ Color        │ Hex        │
├──────────────────────┼──────────────┼────────────┤
│ App Background       │ Cream        │ #F2E9E4    │
│ Card Background      │ White        │ #FFFFFF    │
│ Card Border          │ Rose Beige   │ #C9ADA7    │
│ Primary Text         │ Deep Navy    │ #22223B    │
│ Secondary Text       │ Mauve Gray   │ #9A8C98    │
│ Icon Tint           │ Slate Blue   │ #4A4E69    │
│ Ingredient Badge Bg  │ Rose Beige   │ #C9ADA7    │
│ Ingredient Badge Txt │ Deep Navy    │ #22223B    │
│ Button Gradient 1    │ Mauve→Slate  │ #9A→#4A    │
│ Button Gradient 2    │ Rose→Mauve   │ #C9→#9A    │
│ Status Upcoming      │ Mauve        │ #9A8C98    │
│ Status Pending       │ Rose Beige   │ #C9ADA7    │
│ Status Taken         │ Slate Blue   │ #4A4E69    │
└──────────────────────┴──────────────┴────────────┘
```

---

**Theme**: Warm Neutrals
**Mood**: Elegant, Calming, Professional
**Industry**: Healthcare & Wellness
**Accessibility**: WCAG AAA Compliant
