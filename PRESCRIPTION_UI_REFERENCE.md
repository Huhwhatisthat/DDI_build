# Today's Prescriptions - Visual Reference

## Homepage Layout

```
┌─────────────────────────────────────┐
│  👋 Good morning!                   │
│                                     │
│  ┌────┐  ┌────┐  ┌────┐           │
│  │💊  │  │💊  │  │💊  │  ← Drug   │
│  │Drug│  │Drug│  │Drug│    Cards  │
│  │ 1  │  │ 2  │  │ 3  │           │
│  └────┘  └────┘  └────┘           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Today's Prescriptions              │ ← NEW SECTION
│                                     │
│  ┌──────────────────────────────┐  │
│  │ 🟡 Aspirin           10:30 AM│  │
│  │    1 pill • Every day        │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ ✅ Vitamin D         04:00 PM│  │
│  │    2 pills • Every day       │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Actions                            │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  ➕  Add Medication          │  │
│  │  Scan or enter manually      │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  📋  View Drugs              │  │
│  │  View and check interactions│  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Add Medication Screen (Manual Entry)

```
┌─────────────────────────────────────┐
│  ←  Add Medication                  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Medication Name                    │
│  ┌──────────────────────────────┐  │
│  │ Aspirin                      │  │
│  └──────────────────────────────┘  │
│                                     │
│  How to add ingredient              │
│  ─────────────────────────          │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ 📷 Scan Label            →   │  │
│  │ Use camera to detect...      │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ ✏️  Enter Manually       →   │  │  ← TAPPED
│  │ Type the active ingredient   │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Manual Entry Form (Expanded)

```
┌─────────────────────────────────────┐
│  ←  Add Medication                  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Medication Name                    │
│  ┌──────────────────────────────┐  │
│  │ Aspirin                      │  │
│  └──────────────────────────────┘  │
│                                     │
│  Active Ingredient                  │
│  ┌──────────────────────────────┐  │
│  │ Acetylsalicylic Acid         │  │
│  └──────────────────────────────┘  │
│                                     │
│  ═══ Prescription Details ═══      │  ← NEW SECTION
│                                     │
│  Quantity                           │
│  ┌──────────────────────────────┐  │
│  │ 1                            │  │ ← Number input
│  └──────────────────────────────┘  │
│                                     │
│  Frequency                          │
│  ┌──────────────────────────────┐  │
│  │ Every day               ▼    │  │ ← Dropdown
│  └──────────────────────────────┘  │
│   • Every day                       │
│   • Twice a day                     │
│   • Three times a day               │
│   • Every other day                 │
│   • Once a week                     │
│   • Twice a week                    │
│   • Every month                     │
│   • As needed                       │
│                                     │
│  Time (Optional)                    │
│  ┌──────────────────────────────┐  │
│  │ 10:30 AM              🕐     │  │ ← Time picker
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │      ✓ Save Medication       │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │         Cancel               │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Prescription List Item Breakdown

```
┌────────────────────────────────────────────┐
│  🟡  Aspirin                     10:30 AM  │
│      1 pill • Every day                    │
└────────────────────────────────────────────┘
 │    │                               │
 │    └─ Drug Name (16sp, bold)      └─ Time (14sp, blue)
 │
 └─ Status Icon:
    🟡 = Pending (yellow clock) - future time
    ✅ = Completed (green check) - past time
    
    Quantity & Frequency (14sp, gray)
```

## Color Coding

### Status Icons
- **Pending (Yellow Clock)**: `#FFA726` - Time hasn't arrived yet
- **Completed (Green Check)**: `#66BB6A` - Time has passed

### Text Colors
- **Drug Name**: `#1A1A1A` (Dark, bold)
- **Time**: `#5B7FFF` (Primary blue, prominent)
- **Details**: `#757575` (Gray, secondary)

## Interactive Elements

### Time Picker Dialog
```
┌─────────────────────────┐
│    Select Time          │
│                         │
│      ┌────┬────┐        │
│      │ 10 │ 30 │        │
│      └────┴────┘        │
│                         │
│       ⚪ AM  ⚫ PM       │
│                         │
│   [Cancel]    [OK]      │
└─────────────────────────┘
```

### Frequency Dropdown
```
┌──────────────────────────────┐
│ Every day               ▼    │  ← Tap to open
└──────────────────────────────┘

Opens to:
┌──────────────────────────────┐
│ ✓ Every day                  │  ← Selected
│   Twice a day                │
│   Three times a day          │
│   Every other day            │
│   Once a week                │
│   Twice a week               │
│   Every month                │
│   As needed                  │
└──────────────────────────────┘
```

## Visibility Logic

### When NO prescriptions scheduled
```
Homepage:
- Drug carousel: ✓ Visible
- Prescriptions section: ✗ Hidden (visibility = GONE)
- Actions: ✓ Visible
```

### When prescriptions scheduled
```
Homepage:
- Drug carousel: ✓ Visible
- Prescriptions section: ✓ Visible
- Actions: ✓ Visible
```

## Status Transitions

```
Time: 09:00 AM
Scheduled: 10:30 AM
Status: 🟡 PENDING
─────────────────────

Time: 10:30 AM
Scheduled: 10:30 AM
Status: 🟡 → ✅ (Changes exactly at scheduled time)
─────────────────────

Time: 11:00 AM
Scheduled: 10:30 AM
Status: ✅ COMPLETED
```

## Example Scenarios

### Scenario 1: Morning Medication
```
Drug: "Vitamin D"
Ingredient: "Cholecalciferol"
Quantity: 2
Frequency: "Every day"
Time: "08:00 AM"

Display at 07:30 AM:
┌────────────────────────────────────┐
│ 🟡 Vitamin D           08:00 AM    │
│    2 pills • Every day             │
└────────────────────────────────────┘

Display at 08:15 AM:
┌────────────────────────────────────┐
│ ✅ Vitamin D           08:00 AM    │
│    2 pills • Every day             │
└────────────────────────────────────┘
```

### Scenario 2: Multiple Daily Doses
```
User has 3 medications:

┌────────────────────────────────────┐
│ 🟡 Aspirin             08:00 AM    │ ← Earliest
│    1 pill • Every day              │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ ✅ Vitamin D           12:00 PM    │ ← Already taken
│    2 pills • Every day             │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│ 🟡 Omega-3             06:00 PM    │ ← Evening
│    1 pill • Every day              │
└────────────────────────────────────┘

Sorted by time (08:00 → 12:00 → 18:00)
```

### Scenario 3: No Schedule
```
Drug: "Emergency Inhaler"
Ingredient: "Albuterol"
Quantity: 2
Frequency: "As needed"
Time: "" (empty)

Result: Does NOT appear in Today's Prescriptions
(Only shows in "View Drugs" list)
```

---

**Note**: This visual reference shows the UI layout and behavior. Actual implementation uses Material Design 3 components with proper styling, animations, and touch feedback.
