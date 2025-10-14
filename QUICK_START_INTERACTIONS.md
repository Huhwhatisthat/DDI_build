# 🚀 Quick Start: Testing Drug Interactions

## Step 1: Add Test Drugs

Click **"Add Medication"** and enter:

### Drug 1
- **Name**: Advil
- **Active Ingredient**: `Ibuprofen` ⚠️ (must be exact!)

### Drug 2
- **Name**: Aleve  
- **Active Ingredient**: `Naproxen` ⚠️ (must be exact!)

## Step 2: Check Interactions

1. Go to **"View Drugs"** screen
2. Click **"Check for Interactions"** button (orange warning icon)

## Step 3: See Results

You should see:
- 🔴 **HIGH** severity badge (red)
- **"Ibuprofen ↔ Naproxen"**
- Warning about GI bleeding and kidney damage

---

## ✅ It's Working If You See:

- White drug count badge (top right)
- "Check for Interactions" button (not a switch!)
- Red/Orange/Blue severity badges
- Clear interaction descriptions

---

## ❌ Not Working? Check:

1. **Ingredient spelling** - Must be EXACT:
   - ✅ `Ibuprofen` 
   - ❌ `ibruprofen` (typo)
   - ❌ `Advil` (that's the brand name!)

2. **Entered in correct field** - Use "Active Ingredient" field, not drug name

3. **At least 2 drugs** - Need 2+ drugs to check interactions

---

## 📋 Copy-Paste Ingredient Names

**For Testing**:
```
Ibuprofen
Naproxen
Aspirin
Antacid
Iron
Diphenhydramine
Chlorpheniramine
Pseudoephedrine
Caffeine
Loperamide
Cimetidine
```

---

## 📖 Full Documentation

- **Detailed Guide**: See `DETAILED_INTERACTION_REFERENCE.md`
- **All Test Cases**: See `TESTING_INTERACTIONS.md`
- **Changes Made**: See `UPDATE_SUMMARY.md`
