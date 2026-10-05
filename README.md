# MLBB Skin Script Injector

An Android utility designed to automate the mapping and injection of custom skin assets into *Mobile Legends: Bang Bang* with a single click. 

By utilizing a remote cloud database, this app eliminates manual file downloads, ad-heavy shortlinks, and complex directory browsing.

---

## Key Features

* **Cloud Database Catalog:** Browse and select custom skins directly in-app. All files are hosted on a remote database, bypassing third-party file hosters and shortlinks.
* **1-Click Auto Injection:** Downloads and writes script payloads directly into the target game directory automatically.
* **Multi-Storage Access:** Integrates 3 fallback storage methods to bypass Android Scoped Storage restrictions across different Android versions.

---

## Storage Access Methods (Scoped Storage Handling)

### 1. Standard Mode (`MANAGE_EXTERNAL_STORAGE`)
* **Target:** Primary file management permission for general storage access.
* **Mechanism:** Requests "All Files Access", functioning like a standalone file manager to read/write directly to internal storage without relying on the native SAF picker UI.

### 2. Shizuku Mode (Recommended Non-Root)
* **Target:** Android 11+ devices where system policies restrict `MANAGE_EXTERNAL_STORAGE` from modifying `/Android/data/`.
* **Mechanism:** Leverages **Shizuku** via Wireless Debugging (ADB) to execute elevated file read/write operations inside restricted directories without root.
* **Setup Flow:**
  1. Enable **Developer Options** and **Wireless Debugging** in device settings.
  2. Start the **Shizuku** service.
  3. Grant the application Shizuku access when prompted.

### 3. Root / Superuser Mode (Advanced)
* **Target:** Rooted devices running Magisk, KernelSU, or APatch.
* **Mechanism:** Requests `su` privileges to execute native shell commands (`cp`, `mv`, `chmod`) directly on the filesystem.

---

## Application Workflow

1. **Initialization:** Detects game installation and checks available storage permission in order: **All Files Access → Shizuku → Root**.
2. **Catalog Browsing:** Browse cloud-hosted skins directly inside the app without external browsers or shortlinks.
3. **1-Click Injection:** Tap **Inject** to download and write the script payload into `/Android/data/com.mobile.legends/files/`.

---

> [!NOTE]
> **Game Updates & Maintenance:** Official game updates or running the in-game "Clear Cache" tool will reset custom scripts back to default assets. Re-inject your desired skins in one click after any game patch.
