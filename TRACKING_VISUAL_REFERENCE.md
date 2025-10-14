# Medication Tracking - Visual State Reference

## Complete Status State Flow

```
TIME: 09:00 AM
SCHEDULED: 10:30 AM
═══════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────┐
│  ⚫  Aspirin                          10:30 AM     │
│      1 pill • Every day                            │
│      Status: UPCOMING                              │
│      Color: Gray (#8B94A8)                         │
└────────────────────────────────────────────────────┘
                        │
                        │ Time passes...
                        ↓

TIME: 10:30 AM (Exactly scheduled time)
═══════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────┐
│  🕐  Aspirin                          10:30 AM     │
│      1 pill • Every day                            │
│      Status: PENDING (0 mins)                      │
│      Color: Yellow (#FFA726)                       │
└────────────────────────────────────────────────────┘
                        │
                        │ 3 minutes pass...
                        ↓

TIME: 10:33 AM (3 minutes late)
═══════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────┐
│  🕐  Aspirin                          10:30 AM     │
│      1 pill • Every day                            │
│      Status: PENDING (3 mins)                      │
│      Color: Yellow (#FFA726)                       │
└────────────────────────────────────────────────────┘
                        │
                        │ 3 more minutes pass...
                        ↓

TIME: 10:36 AM (6 minutes late - MISSED!)
═══════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────┐
│  ⚠️   Aspirin                          10:30 AM     │
│      1 pill • Every day                            │
│      Status: MISSED (6 mins)                       │
│      Color: Red (#EF5350)                          │
└────────────────────────────────────────────────────┘
                        │
                        │ User taps item...
                        ↓

USER INTERACTION DIALOG
═══════════════════════════════════════════════════════════

┌────────────────────────────────────────────────────┐
│                                                    │
│  Update Medication Status                         │
│                                                    │
│  Aspirin                            [Blue Bold]   │
│  1 pill • 10:30 AM                 [Gray Normal]  │
│                                                    │
│  Did you take this medication?                    │
│                                                    │
│  ┌──────────────────┐  ┌──────────────────┐      │
│  │                  │  │                  │      │
│  │        ✓         │  │        ✗         │      │
│  │                  │  │                  │      │
│  │      Taken       │  │     Skipped      │      │
│  │                  │  │                  │      │
│  └──────────────────┘  └──────────────────┘      │
│   [Green Gradient]      [Red Gradient]           │
│                                                    │
│  ┌──────────────────────────────────────────┐    │
│  │              Cancel                      │    │
│  └──────────────────────────────────────────┘    │
│                                                    │
└────────────────────────────────────────────────────┘

            ┌─────────────┬─────────────┐
            │             │             │
       [User Clicks]  [User Clicks]    │
         "Taken"        "Skipped"   [Cancel]
            │             │             │
            ↓             ↓             ↓

OUTCOME 1: TAKEN          OUTCOME 2: SKIPPED    OUTCOME 3: No Change
═══════════════════════   ═══════════════════   ═══════════════════

┌──────────────────┐      ┌──────────────────┐  ┌──────────────────┐
│ ✅ Aspirin       │      │ ❌ Aspirin       │  │ ⚠️  Aspirin      │
│ 10:30 AM         │      │ 10:30 AM         │  │ 10:30 AM         │
│ Status: TAKEN    │      │ Status: SKIPPED  │  │ Status: MISSED   │
│ Color: Green     │      │ Color: Red       │  │ Color: Red       │
│ (#66BB6A)        │      │ (#EF5350)        │  │ (#EF5350)        │
└──────────────────┘      └──────────────────┘  └──────────────────┘
  Saved to DB               Saved to DB           No change
  todayStatus=true          todayStatus=false     todayStatus=null
```

## Multi-Medication Dashboard View

```
╔════════════════════════════════════════════════════════╗
║              Today's Prescriptions                     ║
╚════════════════════════════════════════════════════════╝

┌────────────────────────────────────────────────────────┐
│  ⚫  Vitamin D                         08:00 AM        │  ← Gray (Future)
│      2 pills • Every day                               │  09:30 AM now
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  ✅  Aspirin                          09:00 AM        │  ← Green (Taken)
│      1 pill • Every day                                │  User marked
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  🕐  Ibuprofen                        09:15 AM        │  ← Yellow (Pending)
│      2 pills • Twice a day                             │  2 mins ago
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  ⚠️   Metformin                        09:00 AM        │  ← Red (Missed)
│      1 pill • Every day                                │  30 mins ago
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│  ❌  Fish Oil                         08:00 AM        │  ← Red (Skipped)
│      1 pill • Every day                                │  User marked
└────────────────────────────────────────────────────────┘
```

## Status Icon Size Comparison

```
Full Size (32dp)
═════════════════

⚫ UPCOMING      Gray filled circle
🕐 PENDING       Yellow clock with hands
⚠️  MISSED       Red circle with exclamation
✅ TAKEN         Green checkmark in circle
❌ SKIPPED       Red X


Colors:
═══════

UPCOMING   ⚫  #8B94A8  ███████  (Gray - secondary_text)
PENDING    🕐  #FFA726  ███████  (Yellow - warning_color)
MISSED     ⚠️   #EF5350  ███████  (Red - error_color)
TAKEN      ✅  #66BB6A  ███████  (Green - success_color)
SKIPPED    ❌  #EF5350  ███████  (Red - error_color)
```

## Time-Based Status Transitions (Timeline)

```
                    Scheduled Time: 10:30 AM
                            ↓
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                                                        
09:00 AM  │⚫│ UPCOMING     "Not time yet"
09:30 AM  │⚫│ UPCOMING     "Still early"
10:00 AM  │⚫│ UPCOMING     "Almost time"
10:30 AM  │🕐│ PENDING      "It's time!" (0 mins)
10:31 AM  │🕐│ PENDING      "1 minute late"
10:33 AM  │🕐│ PENDING      "3 minutes late"
10:35 AM  │🕐│ PENDING      "5 minutes late" (edge)
10:36 AM  │⚠️ │ MISSED       "6 minutes late!"
10:40 AM  │⚠️ │ MISSED       "10 minutes late"
11:00 AM  │⚠️ │ MISSED       "30 minutes late"
                ↓
         [User marks as Taken]
                ↓
11:00 AM  │✅│ TAKEN        "Confirmed taken"
                ↓
         [Status locked until tomorrow]
```

## Dialog Interaction States

### State 1: Dialog Closed (Default)
```
┌────────────────────────────────────┐
│  ⚠️   Aspirin          10:30 AM    │ ← Tappable item
│      1 pill • Every day            │
└────────────────────────────────────┘
```

### State 2: User Taps Item
```
┌────────────────────────────────────┐
│  ⚠️   Aspirin          10:30 AM    │ ← Ripple effect
│      1 pill • Every day            │
└────────────────────────────────────┘
            ↓
      Dialog opens
```

### State 3: Dialog Visible
```
┌────────────────────────────────────────┐
│  Update Medication Status              │
│                                        │
│  Aspirin                               │
│  1 pill • 10:30 AM                     │
│                                        │
│  Did you take this medication?         │
│                                        │
│  ┌──────────┐  ┌──────────┐          │
│  │   ✓      │  │   ✗      │ ← Buttons │
│  │  Taken   │  │ Skipped  │   active  │
│  └──────────┘  └──────────┘          │
│                                        │
│  ┌──────────────────────────────┐    │
│  │         Cancel               │    │
│  └──────────────────────────────┘    │
└────────────────────────────────────────┘
```

### State 4: User Hovers "Taken" (Touch Feedback)
```
┌────────────────────────────────────────┐
│  Update Medication Status              │
│                                        │
│  Aspirin                               │
│  1 pill • 10:30 AM                     │
│                                        │
│  Did you take this medication?         │
│                                        │
│  ┌──────────┐  ┌──────────┐          │
│  │   ✓      │  │   ✗      │          │
│  │  Taken   │◄─│ Skipped  │ ← Finger │
│  └──────────┘  └──────────┘   here   │
│  (Highlighted)                         │
│                                        │
│  ┌──────────────────────────────┐    │
│  │         Cancel               │    │
│  └──────────────────────────────┘    │
└────────────────────────────────────────┘
```

### State 5: Status Updated, Dialog Dismissed
```
┌────────────────────────────────────┐
│  ✅  Aspirin           10:30 AM    │ ← Updated!
│      1 pill • Every day            │   Green now
└────────────────────────────────────┘
```

## Day Transition Behavior

```
DAY 1 (October 14, 2024)
═══════════════════════════════════════

10:45 AM - User marks Aspirin as TAKEN

Drug State:
{
  name: "Aspirin",
  lastTakenDate: "2024-10-14",
  todayStatus: true
}

Display: ✅ Green checkmark

═══════════════════════════════════════
      ⏰ MIDNIGHT PASSES
═══════════════════════════════════════

DAY 2 (October 15, 2024)
═══════════════════════════════════════

09:00 AM - User opens app

Drug State: (Same data)
{
  name: "Aspirin",
  lastTakenDate: "2024-10-14",  ← OLD DATE
  todayStatus: true              ← IGNORED
}

Status Check:
- lastTakenDate ("2024-10-14") ≠ today ("2024-10-15")
- Ignore todayStatus
- Calculate from time

Display: ⚫ Gray circle (UPCOMING - 10:30 AM not reached)
```

## Error Handling States

### Invalid Time
```
Time parsing fails
↓
Default to UPCOMING status
Show gray icon
```

### Corrupted Status Data
```
todayStatus is invalid (not boolean/null)
↓
Ignore user status
Fall back to time-based calculation
```

### Future Scheduled Time with Past Date
```
Time: 02:00 PM
Scheduled: 10:00 AM
lastTakenDate: "2024-10-14"
Today: "2024-10-14"
todayStatus: true
↓
User status takes priority
Show: ✅ TAKEN
```

## Accessibility

### Screen Reader Announcements

```
⚫ UPCOMING
"Aspirin, 1 pill, scheduled for 10:30 AM, status: upcoming"

🕐 PENDING
"Aspirin, 1 pill, scheduled for 10:30 AM, status: pending, due now"

⚠️  MISSED
"Aspirin, 1 pill, scheduled for 10:30 AM, status: missed, overdue"

✅ TAKEN
"Aspirin, 1 pill, scheduled for 10:30 AM, status: taken, completed"

❌ SKIPPED
"Aspirin, 1 pill, scheduled for 10:30 AM, status: skipped"
```

### Button Labels

```
Dialog Buttons:
- "Mark as taken" (green button)
- "Mark as skipped" (red button)
- "Cancel update" (cancel button)
```

## Animation Recommendations

### Status Change Animation
```
Before:  ⚠️  (Red - Missed)
            ↓
         [Fade out]
            ↓
         [Scale up]
            ↓
After:   ✅  (Green - Taken)
```

### Dialog Appearance
```
List item clicked
     ↓
Ripple effect
     ↓
Fade in overlay (0.5s)
     ↓
Slide up dialog (0.3s)
     ↓
Dialog visible
```

### List Refresh
```
Dialog closes
     ↓
Quick fade (0.2s)
     ↓
Item updates in-place
     ↓
Subtle pulse effect (0.3s)
```

---

**Visual Reference Guide**
**Version**: 2.0
**Last Updated**: 2024
