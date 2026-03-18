# How to Export the APK

Follow these steps to generate a debug APK that you can share with others for testing. Make sure to run these commands in the terminal at the root of your project (`c:\Users\USER\Downloads\whatsapp-tracker`).

## Step 1: Clean the Project (Optional but recommended)
It's a good practice to clean any previous build artifacts before creating a fresh build.
```powershell
.\gradlew clean
```

*(Wait for this command to finish and provide the result if there are any issues).*

## Step 2: Build the Debug APK
Next, run the command to assemble the debug APK. A debug APK is the easiest to share because it doesn't require setting up a production signing key.
```powershell
.\gradlew assembleDebug
```

*(Let me know if this command succeeds or fails).*

## Step 3: Locate the APK
Once the build finishes successfully, your new APK will be located here:
`app\build\outputs\apk\debug\app-debug.apk`

You can take this `app-debug.apk` file and send it to your friends! Note that they may need to allow "Install from Unknown Sources" on their Android devices to install it.

## Troubleshooting Installation & Permissions

Because this is an app installed from an unknown source (sideloaded) and it requests a sensitive permission (Accessibility Service to read screen contents), Android will aggressively try to block it. Here is how to instruct your testers to bypass these blocks:

### 1. "App blocked to protect your device" (Play Protect)
If they get a popup like your screenshot saying **Google Play Protect blocked the app**:
1. Open the **Google Play Store**.
2. Tap their **Profile icon** in the top right.
3. Tap **Play Protect** -> Settings (the **Gear icon** in the top right).
4. Turn **OFF** "Scan apps with Play Protect".
5. They can now install your APK. They can turn this setting back on after installation.

> *Note: Sometimes Play Protect gives a "More details" dropdown right on the blocked popup, which contains an "Install anyway" option. If they see that, they can just click that instead.*

### 2. "Restricted Setting" Dialog (Android 13+)
Once the app is installed, they need to go into the app and try to enable the Accessibility Service. On Android 13 or newer, they will likely get a grey popup saying **"Restricted setting - For your security, this setting is currently unavailable."**

To bypass this and grant the permission:
1. Open their phone's main **Settings** app.
2. Go to **Apps** and find **WhatsAppTrackerApp**.
3. On the App Info page, tap the **3 vertical dots** in the top right corner.
4. Tap **"Allow restricted settings"**.
5. They may be asked to scan their fingerprint or enter their PIN.
6. Now, they can go back to the app, tap the switch to enable tracking, and the Accessibility permission screen will finally let them turn it on!
