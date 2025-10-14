# Testing the Interactions Feature

## ⚠️ CRITICAL: Use Exact Ingredient Names!

The app matches interactions based on **active ingredients**, not drug brand names. You MUST enter the ingredient names exactly as shown below (case doesn't matter).

## Quick Test Guide

### ✅ Test Case 1: HIGH Severity Interaction (NSAID Overdose)

**Setup:**
1. Add Drug: Name = "Advil", Ingredient = `Ibuprofen`
2. Add Drug: Name = "Aleve", Ingredient = `Naproxen`

**Expected Result:**
- **Severity**: RED badge "HIGH"
- **Interaction**: "Ibuprofen ↔ Naproxen"
- **Description**: "Increased risk of severe gastrointestinal bleeding and kidney damage"

---

### ✅ Test Case 2: HIGH Severity Interaction (Cardiac Risk)

**Setup:**
1. Add Drug: Name = "Imodium", Ingredient = `Loperamide`
2. Add Drug: Name = "Tagamet", Ingredient = `Cimetidine`

**Expected Result:**
- **Severity**: RED badge "HIGH"
- **Interaction**: "Loperamide ↔ Cimetidine"
- **Description**: "Increased loperamide levels, potential for serious cardiac side effects"

---

### ✅ Test Case 3: MODERATE Severity Interaction (Iron Absorption)

**Setup:**
1. Add Drug: Name = "Tums", Ingredient = `Antacid`
2. Add Drug: Name = "Iron Pills", Ingredient = `Iron`

**Expected Result:**
- **Severity**: ORANGE badge "MODERATE"
- **Interaction**: "Antacid ↔ Iron Supplement"
- **Description**: "Reduced iron absorption, leading to treatment failure for anemia"

---

### ✅ Test Case 4: MODERATE Severity Interaction (Stimulants)

**Setup:**
1. Add Drug: Name = "Sudafed", Ingredient = `Pseudoephedrine`
2. Add Drug: Name = "Coffee", Ingredient = `Caffeine`

**Expected Result:**
- **Severity**: ORANGE badge "MODERATE"
- **Interaction**: "Pseudoephedrine ↔ Caffeine"
- **Description**: "Increased heart rate, blood pressure, anxiety, and insomnia"

---

### ✅ Test Case 5: MODERATE Severity Interaction (Drowsiness)

**Setup:**
1. Add Drug: Name = "Benadryl", Ingredient = `Diphenhydramine`
2. Add Drug: Name = "Cold Medicine", Ingredient = `Chlorpheniramine`

**Expected Result:**
- **Severity**: ORANGE badge "MODERATE"
- **Interaction**: "Diphenhydramine ↔ Chlorpheniramine"
- **Description**: "Severe drowsiness, impaired coordination, increased risk of accidents"

---

### ✅ Test Case 6: LOW Severity Interaction

**Setup:**
1. Add Drug: Name = "Tums", Ingredient = `Antacid`
2. Add Drug: Name = "Bayer", Ingredient = `Aspirin`

**Expected Result:**
- **Severity**: BLUE badge "MINOR"
- **Interaction**: "Antacid ↔ Aspirin"
- **Description**: "Decreased absorption and effectiveness of aspirin for pain relief"

**Alternative Aspirin Names:**
- You can also use `Acetylsalicylic Acid` as the ingredient

---

### ✅ Test Case 7: No Interactions (Safe Combination)

**Setup:**
1. Add Drug: Name = "Tylenol", Ingredient = `Paracetamol`
2. Add Drug: Name = "Vitamin C", Ingredient = `Ascorbic Acid`

**Expected Result:**
- Green checkmark icon
- Message: "No Interactions Found"
- Subtext: "Your current medications are safe to take together"

---

## 📋 Complete List of Exact Ingredient Names

Use these EXACT spellings (case-insensitive):

### NSAIDs:
- `Ibuprofen`
- `Naproxen`
- `Aspirin` OR `Acetylsalicylic Acid`

### Antihistamines:
- `Diphenhydramine`
- `Chlorpheniramine`

### Cough/Cold:
- `Dextromethorphan`
- `Pseudoephedrine`

### Digestive:
- `Antacid`
- `Loperamide`
- `Cimetidine`
- `Famotidine`
- `Omeprazole`
- `Bisacodyl`
- `Senna`
- `Lactulose`

### Supplements:
- `Iron` OR `Ferrous Sulfate`
- `Caffeine`

### Pain Relievers:
- `Paracetamol` (Tylenol)

---

## 🧪 Step-by-Step Testing Process

### Step 1: Add Test Drugs
1. Open the app
2. Click **"Add Medication"**
3. Enter drug name (can be anything, e.g., "Test Drug A")
4. **IMPORTANT**: Enter active ingredient EXACTLY as shown above
5. Save the drug
6. Repeat for second drug

### Step 2: Check for Interactions
1. Go to **"View Drugs"** screen
2. You should see both drugs listed
3. Click **"Check for Interactions"** button (orange warning icon)
4. New page opens: **"Drug Interactions"**

### Step 3: Verify Results

**If Interactions Found:**
- Count badge shows correct number
- Interaction cards display with:
  - ✅ Correct drug pair names
  - ✅ Color-coded severity badge
  - ✅ Clear description
- Scroll to see all interactions

**If No Interactions:**
- Green checkmark appears
- "No Interactions Found" message
- "Your current medications are safe to take together"

---

## ❌ Common Issues & Solutions

### Issue: "No Interactions Found" when there should be

**Problem**: Ingredient name doesn't match database

**Solution**: 
- Double-check spelling
- Use exact names from list above
- Check for extra spaces
- Try alternative names (e.g., `Aspirin` vs `Acetylsalicylic Acid`)

### Issue: Button is disabled/grayed out

**Problem**: Less than 2 drugs added

**Solution**: Add at least 2 drugs to check for interactions

### Issue: App crashes when clicking button

**Problem**: Missing activity or layout files

**Solution**: Rebuild the app

---

## 🎯 What You Should See

### View Drugs Screen:
- ✅ List of your medications
- ✅ Each shows drug name + active ingredient
- ✅ White count badge (top right)
- ✅ **"Check for Interactions"** button with warning icon and arrow
- ✅ Button disabled if less than 2 drugs

### Interactions Screen:
- ✅ Header: "Drug Interactions"
- ✅ Orange count badge showing number of interactions
- ✅ Interaction cards with severity badges
- ✅ OR green checkmark with "No Interactions Found"

### Interaction Card Details:
- ✅ Warning icon with light background
- ✅ Severity badge (RED/ORANGE/BLUE)
- ✅ Two drug names with ↔ arrow between them
- ✅ Clear description of the risk

---

## 📊 Testing Checklist

- [ ] Can add drugs with correct ingredient names
- [ ] Drug count badge shows white text
- [ ] "Check for Interactions" button appears (not switch)
- [ ] Button opens new page
- [ ] HIGH severity shows RED badge
- [ ] MODERATE severity shows ORANGE badge  
- [ ] MINOR severity shows BLUE badge
- [ ] No interactions shows green checkmark
- [ ] Back button returns to View Drugs
- [ ] Can delete drugs and re-check
- [ ] Multiple interactions display correctly

---

## 💡 Pro Tips

1. **Start Simple**: Test with just 2 drugs first (Ibuprofen + Naproxen)
2. **Exact Names**: Copy-paste ingredient names from this document
3. **Check Colors**: Verify severity badges match expected colors
4. **Test Empty**: Delete all drugs to see empty state
5. **Multiple Interactions**: Add 3+ interacting drugs to test multiple results

---

## 📞 Need Help?

If interactions still aren't showing:
1. Verify ingredient names EXACTLY match the list
2. Check that you're entering ingredients (not brand names) in the "Active Ingredient" field
3. Ensure at least 2 drugs are added
4. Try rebuilding the app
5. Check logs for any error messages
