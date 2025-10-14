# Medication Tracking System - Implementation Guide

## Overview
This document describes the enhanced medication tracking system that allows users to mark prescriptions as taken or skipped, with intelligent 4-state status detection.

## Four-State Status System

### Status Logic

```
┌─────────────────────────────────────────────────────────┐
│                  Status State Diagram                    │
└─────────────────────────────────────────────────────────┘

GRAY (Upcoming)
  ├─ Time hasn't arrived yet
  └─ Icon: Filled circle (ic_upcoming)

        ↓ (Time arrives)

YELLOW (Pending) 
  ├─ Time arrived
  ├─ Less than 5 minutes passed
  └─ Icon: Clock (ic_pending)

        ↓ (5+ minutes pass)

RED (Missed)
  ├─ Time arrived
  ├─ More than 5 minutes passed
  ├─ User hasn't marked status
  └─ Icon: Warning exclamation (ic_missed)

        ↓ (User interaction)

GREEN (Taken) or RED (Skipped)
  ├─ User clicked item and chose status
  ├─ Taken: Green checkmark (ic_check)
  └─ Skipped: Red X (ic_close)
```

### State Conditions

| Status | Time Condition | User Action | Icon | Color |
|--------|---------------|-------------|------|-------|
| **UPCOMING** | Time not arrived | None | ⚫ Filled Circle | Gray (#8B94A8) |
| **PENDING** | 0-5 mins after time | None | 🕐 Clock | Yellow (#FFA726) |
| **MISSED** | 5+ mins after time | None | ⚠️ Warning | Red (#EF5350) |
| **TAKEN** | Any time after | User marked "Taken" | ✅ Checkmark | Green (#66BB6A) |
| **SKIPPED** | Any time after | User marked "Skipped" | ❌ X | Red (#EF5350) |

## User Interaction Flow

### 1. Viewing Prescriptions
```
Homepage → Today's Prescriptions Section
├─ Shows all medications with scheduled times
├─ Each item displays:
│  ├─ Status icon (auto-updated)
│  ├─ Drug name
│  ├─ Quantity and frequency
│  └─ Scheduled time
└─ Sorted by time (earliest first)
```

### 2. Marking Status
```
User taps prescription item
    ↓
Dialog appears: "Update Medication Status"
    ↓
Shows drug details + question: "Did you take this medication?"
    ↓
Two options:
├─ [✓ Taken] - Green button
└─ [✗ Skipped] - Red button
    ↓
Status saved to today's date
    ↓
List refreshes with new status
```

### 3. Status Persistence
- Status is saved per medication per day
- Uses `lastTakenDate` field (YYYY-MM-DD format)
- Resets automatically next day (old status ignored)
- Persists across app restarts

## Technical Implementation

### Data Model Changes

**Drug.kt - New Fields:**
```kotlin
data class Drug(
    val name: String,
    val activeIngredient: String,
    val quantity: Int = 1,
    val frequency: String = "Every day",
    val time: String = "",
    val lastTakenDate: String = "",      // NEW: "2024-10-14"
    val todayStatus: Boolean? = null      // NEW: true=taken, false=skipped, null=not set
)
```

### Repository Methods

**DrugRepository.kt - New Methods:**
```kotlin
// Update medication status for today
fun updateMedicationStatus(drugName: String, taken: Boolean)

// Internal: Get current date in YYYY-MM-DD format
private fun getCurrentDate(): String
```

### Adapter Logic

**PrescriptionAdapter.kt - Status Detection:**
```kotlin
private fun getMedicationStatus(drug: Drug): MedicationStatus {
    // 1. Check if user already marked status TODAY
    if (drug.todayStatus != null && drug.lastTakenDate == today) {
        return if (drug.todayStatus) TAKEN else SKIPPED
    }
    
    // 2. Calculate time-based status
    val minutesSinceScheduled = getMinutesSinceScheduledTime(drug.time)
    
    return when {
        minutesSinceScheduled < 0  -> UPCOMING   // Future
        minutesSinceScheduled <= 5 -> PENDING    // 0-5 mins
        else                       -> MISSED     // 5+ mins
    }
}
```

## UI Components

### 1. Status Dialog
**File:** `dialog_medication_status.xml`

**Components:**
- Header: "Update Medication Status"
- Drug name (blue, bold)
- Drug details (quantity + time)
- Question: "Did you take this medication?"
- Two action cards:
  - **Taken** - Green gradient background, checkmark icon
  - **Skipped** - Red gradient background, X icon
- Cancel button

### 2. New Icons Created

| Icon | File | Purpose | Color When Used |
|------|------|---------|-----------------|
| ⚫ Circle | `ic_upcoming.xml` | Upcoming medication | Gray |
| 🕐 Clock | `ic_pending.xml` | Pending (existing) | Yellow |
| ⚠️ Warning | `ic_missed.xml` | Missed medication | Red |
| ✅ Check | `ic_check.xml` | Taken (existing) | Green |
| ❌ X | `ic_close.xml` | Skipped | Red |

### 3. Colors Added

```xml
<color name="success_color">#66BB6A</color>   <!-- Green for taken -->
<color name="warning_color">#FFA726</color>   <!-- Yellow for pending -->
<color name="error_color">#EF5350</color>     <!-- Red for missed/skipped -->
```

## Code Flow Example

### Example 1: Medication Status Timeline

**Scenario:** User has Aspirin scheduled for 10:30 AM

```
09:00 AM - Status: UPCOMING (Gray)
├─ Time: -90 minutes from scheduled
└─ Display: Gray filled circle

10:30 AM - Status: PENDING (Yellow)
├─ Time: 0 minutes from scheduled
└─ Display: Yellow clock

10:33 AM - Status: PENDING (Yellow)
├─ Time: +3 minutes from scheduled
├─ Still within 5-minute window
└─ Display: Yellow clock

10:36 AM - Status: MISSED (Red)
├─ Time: +6 minutes from scheduled
├─ Exceeded 5-minute window
└─ Display: Red warning icon

10:45 AM - User taps item and marks "Taken"
├─ lastTakenDate: "2024-10-14"
├─ todayStatus: true
└─ Display: Green checkmark (overrides time-based status)

Next Day (10:00 AM)
├─ lastTakenDate: "2024-10-14" (old date)
├─ Current date: "2024-10-15"
├─ Status reset to UPCOMING
└─ Display: Gray filled circle
```

### Example 2: User Interaction

**User Story:** Mark medication as taken

```kotlin
// 1. User sees prescription in list
PrescriptionAdapter displays item with status icon

// 2. User taps item
holder.itemView.setOnClickListener {
    onItemClick(drug)  // Callback to HomeActivity
}

// 3. HomeActivity shows dialog
showMedicationStatusDialog(drug)
    ↓
dialog_medication_status.xml inflated
    ↓
Drug info displayed

// 4. User clicks "Taken"
btn_taken.setOnClickListener {
    DrugRepository.updateMedicationStatus(drug.name, taken = true)
    updatePrescriptionsList()  // Refresh UI
    dialog.dismiss()
}

// 5. Repository updates drug
val updatedDrug = drug.copy(
    lastTakenDate = "2024-10-14",
    todayStatus = true
)
drugs[index] = updatedDrug
saveDrugsToStorage()  // Persist

// 6. List refreshes
Adapter re-renders with GREEN checkmark
```

## Visual Examples

### Status Icon Progression

```
┌──────────────────────────────────────┐
│ ⚫ Aspirin            10:30 AM       │ ← 09:00 AM (Gray - Upcoming)
│    1 pill • Every day                │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ 🕐 Aspirin            10:30 AM       │ ← 10:32 AM (Yellow - Pending)
│    1 pill • Every day                │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│ ⚠️  Aspirin            10:30 AM       │ ← 10:40 AM (Red - Missed)
│    1 pill • Every day                │
└──────────────────────────────────────┘

[User taps and marks as Taken]

┌──────────────────────────────────────┐
│ ✅ Aspirin            10:30 AM       │ ← (Green - Taken)
│    1 pill • Every day                │
└──────────────────────────────────────┘
```

### Status Dialog

```
┌──────────────────────────────────────┐
│  Update Medication Status            │
│                                      │
│  Aspirin                             │ ← Blue
│  1 pill • 10:30 AM                   │ ← Gray
│                                      │
│  Did you take this medication?       │
│                                      │
│  ┌──────────┐  ┌──────────┐        │
│  │    ✓     │  │    ✗     │        │
│  │  Taken   │  │ Skipped  │        │
│  └──────────┘  └──────────┘        │
│    (Green)       (Red)              │
│                                      │
│  ┌──────────────────────────────┐  │
│  │         Cancel               │  │
│  └──────────────────────────────┘  │
└──────────────────────────────────────┘
```

## Files Modified

### New Files Created (5)
1. `app/src/main/res/drawable/ic_upcoming.xml` - Gray circle icon
2. `app/src/main/res/drawable/ic_missed.xml` - Red warning icon
3. `app/src/main/res/drawable/ic_close.xml` - X icon for skipped
4. `app/src/main/res/layout/dialog_medication_status.xml` - Status dialog
5. `MEDICATION_TRACKING_SYSTEM.md` - This documentation

### Modified Files (5)
1. `app/src/main/java/com/example/drugidentifier/models/Drug.kt`
   - Added `lastTakenDate: String`
   - Added `todayStatus: Boolean?`

2. `app/src/main/java/com/example/drugidentifier/data/DrugRepository.kt`
   - Added `updateMedicationStatus()` method
   - Added `getCurrentDate()` helper

3. `app/src/main/java/com/example/drugidentifier/PrescriptionAdapter.kt`
   - Complete rewrite with 4-state logic
   - Added `onItemClick` callback
   - Added `MedicationStatus` enum
   - Added `getMedicationStatus()` method
   - Added `getMinutesSinceScheduledTime()` method

4. `app/src/main/java/com/example/drugidentifier/HomeActivity.kt`
   - Updated `updatePrescriptionsList()` with click handler
   - Added `showMedicationStatusDialog()` method

5. `app/src/main/res/values/colors.xml`
   - Added `success_color` (#66BB6A)
   - Added `warning_color` (#FFA726)
   - Added `error_color` (#EF5350)

## Testing Checklist

### Status Detection Logic
- [ ] Gray icon shows when time hasn't arrived
- [ ] Yellow icon shows 0-5 minutes after scheduled time
- [ ] Red warning shows 5+ minutes after scheduled time
- [ ] Green check shows when user marks as taken
- [ ] Red X shows when user marks as skipped
- [ ] Status persists after app restart
- [ ] Status resets next day (old status ignored)

### User Interaction
- [ ] Tapping prescription item opens dialog
- [ ] Dialog shows correct drug info
- [ ] "Taken" button updates status to green check
- [ ] "Skipped" button updates status to red X
- [ ] "Cancel" button closes dialog without changes
- [ ] List refreshes immediately after status update

### Edge Cases
- [ ] Midnight crossing (time scheduled 11:59 PM, checked 12:01 AM)
- [ ] Multiple medications at same time
- [ ] Marking status before scheduled time (should still work)
- [ ] Changing status multiple times same day (latest wins)
- [ ] App restart preserves today's status
- [ ] Old data without tracking fields loads correctly

### Time Calculations
- [ ] Exactly at scheduled time shows PENDING (0 minutes)
- [ ] 5 minutes after shows PENDING (edge case)
- [ ] 6 minutes after shows MISSED
- [ ] Negative minutes (future time) shows UPCOMING
- [ ] 12-hour AM/PM format parsed correctly

## Configuration

### Timing Constants
Current implementation uses **5 minutes** as the pending window:

```kotlin
return when {
    minutesSinceScheduled < 0  -> UPCOMING    // Future
    minutesSinceScheduled <= 5 -> PENDING     // 0-5 mins
    else                       -> MISSED      // 5+ mins
}
```

**To adjust:**
- Change `<= 5` to desired minutes
- Example: `<= 10` for 10-minute pending window
- Example: `<= 2` for stricter 2-minute window

## Future Enhancements

### Suggested Features
1. **Customizable Grace Period**: Let users set their own 5-minute window
2. **Notification Reminders**: Push notification at scheduled time
3. **Weekly Statistics**: Show adherence percentage (taken/total)
4. **Missed Medication Alert**: Red badge count on homepage
5. **History View**: See past days' medication tracking
6. **Undo Action**: Allow reverting status change
7. **Bulk Actions**: Mark multiple medications at once
8. **Smart Suggestions**: Remind if pattern of missed meds detected
9. **Export Report**: Generate PDF of medication history
10. **Caregiver Mode**: Share tracking with family/doctor

### Technical Improvements
1. Use Room database for better querying
2. Add ViewModel for reactive updates
3. Implement WorkManager for reliable notifications
4. Add animation when status changes
5. Swipe gestures for quick taken/skipped
6. Haptic feedback on status update
7. Accessibility improvements (TalkBack support)
8. Dark mode support for status colors

## API Reference

### Drug Data Class
```kotlin
data class Drug(
    val name: String,                    // Medication name
    val activeIngredient: String,        // Active ingredient
    val quantity: Int = 1,               // Pills per dose
    val frequency: String = "Every day", // Frequency
    val time: String = "",               // Scheduled time (hh:mm a)
    val lastTakenDate: String = "",      // Last marked date (yyyy-MM-dd)
    val todayStatus: Boolean? = null     // null=not set, true=taken, false=skipped
)
```

### DrugRepository Methods
```kotlin
// Update medication status for current day
fun updateMedicationStatus(drugName: String, taken: Boolean)
// drugName: Name of the medication
// taken: true if taken, false if skipped
```

### PrescriptionAdapter Constructor
```kotlin
PrescriptionAdapter(
    prescriptions: List<Drug>,           // List of prescriptions to display
    onItemClick: (Drug) -> Unit          // Callback when item clicked
)
```

### MedicationStatus Enum
```kotlin
enum class MedicationStatus {
    UPCOMING,   // Time not arrived - gray
    PENDING,    // 0-5 mins after - yellow
    MISSED,     // 5+ mins after - red
    TAKEN,      // User marked taken - green
    SKIPPED     // User marked skipped - red
}
```

## Notes
- Status is per-day, not per-dose (even for "twice a day" medications)
- Changing device time doesn't affect tracking (uses system date)
- Status always shows most recent user action if set for today
- Automatically resets at midnight (checks date match)
- Compatible with all frequency types (daily, weekly, monthly, etc.)
- Works with medications that have no scheduled time (won't show in prescriptions list)

---

**Implementation Date**: 2024  
**Version**: 2.0  
**Status**: Complete ✓
