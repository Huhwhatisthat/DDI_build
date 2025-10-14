# Testing the Interactions Feature

## Quick Test Guide

To test if the interactions feature is working properly, add these sample drugs:

### Test Case 1: HIGH Severity Interaction
1. **Drug 1**: "Advil" with ingredient "Ibuprofen"
2. **Drug 2**: "Aleve" with ingredient "Naproxen"
3. **Expected**: Shows HIGH severity warning about GI bleeding and kidney damage

### Test Case 2: MODERATE Severity Interaction
1. **Drug 1**: "Sudafed" with ingredient "Pseudoephedrine"
2. **Drug 2**: "Coffee" with ingredient "Caffeine"
3. **Expected**: Shows MODERATE warning about increased heart rate and blood pressure

### Test Case 3: LOW Severity Interaction
1. **Drug 1**: "Tums" with ingredient "Antacid"
2. **Drug 2**: "Aspirin" with ingredient "Aspirin"
3. **Expected**: Shows LOW/MINOR warning about reduced aspirin effectiveness

### Test Case 4: No Interactions
1. **Drug 1**: "Tylenol" with ingredient "Paracetamol"
2. **Drug 2**: "Vitamin C" with ingredient "Ascorbic Acid"
3. **Expected**: Shows "No Interactions Found" with green checkmark

## How to Test

1. Open the app
2. Click "Add Medication"
3. Add the test drugs one by one
4. Go to "View Drugs"
5. Toggle the "Show Interactions" switch
6. The Interactions page should open showing any found interactions

## What to Look For

✅ **Working Correctly**:
- Switch opens new page
- Page shows interaction cards with severity badges
- Colors match severity (Red=HIGH, Orange=MODERATE, Blue=LOW)
- Description is clear and helpful
- Empty state shows when no interactions

❌ **Not Working**:
- Switch does nothing
- Page crashes
- No interactions shown when they should be
- Wrong severity colors
