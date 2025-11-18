# GitHub Secrets Setup Guide

## Overview
This guide explains how to add the signing keystore to GitHub Secrets so that CI builds can sign APKs with your official certificate.

## Why This Is Safe

- ✅ Keystore file is **encrypted** at rest on GitHub
- ✅ Only accessible during CI builds  
- ✅ Never exposed in logs or build output
- ✅ Only you can manage these secrets
- ✅ APKs signed with this key are identical to what you build locally

## Step-by-Step Setup

### 1. Go to GitHub Secrets Settings
- Navigate to: https://github.com/YOUR_USERNAME/DrillDownEx
- Go to: **Settings** → **Secrets and variables** → **Actions**
- Click: **"New repository secret"**

### 2. Add Four Secrets

**First Secret:**
- **Name:** `KEYSTORE_FILE`
- **Value:** Copy the entire contents of `keystore_base64.txt` (the full base64 string)

**Second Secret:**
- **Name:** `KEYSTORE_PASSWORD`
- **Value:** `drilldown123`

**Third Secret:**
- **Name:** `KEY_ALIAS`
- **Value:** `drilldown`

**Fourth Secret:**
- **Name:** `KEY_PASSWORD`
- **Value:** `drilldown123`

### 3. Delete Local Files (Recommended)
After adding the secrets to GitHub, delete the local keystore backup file:
```bash
rm keystore_base64.txt
```

This prevents the encoded keystore from being accidentally committed.

## Verification

After setting up the secrets:
1. Push any change to main branch
2. Go to: **Actions** tab on GitHub
3. Watch the CI workflow run
4. Check the **"Set up signing keystore"** step - it should show "✓ Passed"
5. The APK will be signed with your keystore

## What Happens Now

### Local Builds (Your Computer)
- ✅ Same as before - uses your local `drilldown.keystore`
- ✅ Your app data and settings are preserved when updating

### CI Builds (GitHub)
- ✅ Automatically decodes keystore from secrets
- ✅ Builds signed release APK
- ✅ APK can be installed over your existing app
- ✅ Your app data and settings are preserved

### Distribution (App Stores)
- ✅ APKs are all signed with the same key
- ✅ Users can install updates without issues
- ✅ Your certificate identity is verified

## Security Best Practices

1. ✅ Store keystore passwords in GitHub Secrets, not in code
2. ✅ Never commit `keystore_base64.txt` to the repository
3. ✅ Only add secrets to your own repositories
4. ✅ Rotate secrets if anyone else had access to them
5. ✅ Keep your GitHub account secure (use 2FA)

## Troubleshooting

**Problem:** "Error: failed to read secrets"
- Solution: Check that all secret names are spelled exactly as shown above

**Problem:** "Invalid keystore format"
- Solution: Make sure you copied the ENTIRE contents of `keystore_base64.txt` without adding/removing characters

**Problem:** "APK not installing - signature mismatch"
- Solution: This won't happen with this setup - both CI and local builds use the same keystore

## Questions?

If you have issues, check:
1. CI workflow output: GitHub → Actions → Latest run
2. Keystore file exists: `android/drilldown.keystore`
3. All secrets are added correctly (check spelling)

