# Data Backup Application

## Introduction
The Data Backup Application is designed to automatically back up files that haven't been accessed within a specific time frame, thereby helping users free up space and secure their data without manual intervention. It scans the device's Downloads directory and uploads eligible files to Firebase Storage, respecting device constraints like battery levels and network availability.

## Key Features
1. **Backup Old Files:** Identifies and backs up files not accessed within a defined time frame.
2. **Firebase Integration:** Utilizes Firebase Storage for storing backed-up files and Firebase Database for tracking file metadata.
3. **Progress Display:** Displays a progress bar to indicate the current backup operations' status.
4. **Dynamic Permissions Handling:** Requests and checks necessary permissions at runtime for smooth operation.
5. **Device Status Checks:** Verifies device battery and network connectivity before initiating backup tasks.
6. **Sanitization of File Paths:** Ensures filenames meet Firebase path requirements, avoiding common pitfalls.

## Components
### MainActivity
- Entry point of the application.
- Hosts the primary user interface for navigation and initiating backup/restore processes.
- Utilizes Android lifecycle for efficient management of background tasks.

### BackupActivity
- Core UI component handling backup operations and progress display.
- Manages backup process dynamically using state holders.

### RestoreActivity
- Manages user interface and functionality for restoring files from Firebase Storage.

### FileEntity
- Represents metadata of a file, including name, path, and backup status.

### FileRepository
- Manages interactions with the local database to store and retrieve file metadata.

### FirebaseDBHelper
- Provides methods for interacting with Firebase, including uploading files and updating metadata.

### DeviceStatusUtils
- Utility class for checking device-specific constraints like battery level and network availability.

## Operations
1. **Permission Checks:** Ensures necessary permissions are granted at startup.
2. **File Scanning:** Scans Downloads directory for eligible files.
3. **Backup Execution:** Uploads files to Firebase Storage while updating local and Firebase databases.
4. **Progress Monitoring:** Updates backup progress in real-time on the UI.
5. **Error Handling:** Provides robust error handling throughout the backup process.

## Key Technologies and Practices
- **Kotlin:** Primary programming language, offering concise syntax and null safety.
- **Android Jetpack Compose:** Declarative UI development for responsive and dynamic interfaces.
- **Firebase Storage & Realtime Database:** Cloud storage and metadata management.
- **Coroutines & Kotlin Flow:** Asynchronous task management and real-time data flow.
- **Material Design Components:** Designing consistent and professional UIs.
- **Dependency Injection (Potential with Hilt/Dagger):** Simplifies dependency management for easier testing.

## References
- [Android Developer Documentation](https://developer.android.com/docs)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Firebase Documentation](https://firebase.google.com/docs)

