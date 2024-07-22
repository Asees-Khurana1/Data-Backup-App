# DataBackupApp

DataBackupApp is an Android application designed to backup and restore data efficiently. It leverages Jetpack Compose for the UI and follows SOLID principles and best practices from the Android Developers website.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Architecture](#architecture)
- [Components](#components)
  - [MainActivity](#mainactivity)
  - [BackupActivity](#backupactivity)
  - [RestoreActivity](#restoreactivity)
  - [BackupService](#backupservice)
  - [BackupWorker](#backupworker)
  - [FileRepository](#filerepository)
  - [FirebaseDBHelper](#firebasedbhelper)
  - [DeviceStatus](#devicestatus)
  - [SensorHandler](#sensorhandler)

## Features

- Backup data to Firebase
- Restore data from Firebase
- Background backup operations
- Sensor data management
- Device status checks

## Getting Started

### Prerequisites

- Android Studio 4.1 or higher
- Java 8 or higher
- Firebase account

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/DataBackupApp.git
    ```
2. Open the project in Android Studio.
3. Connect your Firebase account and configure the `google-services.json` file.
4. Build and run the app on your Android device or emulator.

## Architecture

The application is designed following SOLID principles and best practices from the Android Developers website, ensuring a clean and maintainable codebase.

## Components

### MainActivity

#### Purpose

MainActivity is the primary entry point of the application, setting up the initial UI and handling primary user interactions.

#### Functionality

- **Initializes UI**: Sets the content view using the layout file `activity_main.xml`.
- **User Interaction**: Sets up buttons and click listeners for user actions.
- **Navigation**: Provides navigation options to `BackupActivity` and `RestoreActivity` via button clicks.
- **Lifecycle Management**: Manages the activity lifecycle and ensures proper resource handling during lifecycle events.

### BackupActivity

#### Purpose

Handles the backup operations of the app.

#### Functionality

- **UI Setup**: Sets the content view using the layout file `activity_backup.xml`.
- **Backup Initialization**: Starts the backup process when the user clicks the backup button.
- **Progress Display**: Updates the user interface with the progress and status of the backup operation using LiveData observers.

### RestoreActivity

#### Purpose

Manages the restoration of data from backup.

#### Functionality

- **UI Setup**: Sets the content view using the layout file `activity_restore.xml`.
- **Restore Initialization**: Starts the restore process when the user clicks the restore button.
- **Progress Display**: Updates the user interface with the progress and status of the restore operation using LiveData observers.

### BackupService

#### Purpose

A service that handles background backup tasks.

#### Functionality

- **Background Operations**: Runs backup operations in the background to ensure the main UI remains responsive.
- **Worker Utilization**: Initiates `BackupWorker` to perform the actual backup tasks.
- **Service Lifecycle**: Manages the lifecycle of the service to ensure it starts and stops correctly based on user actions and system conditions.

### BackupWorker

#### Purpose

Executes the backup operations.

#### Functionality

- **File Operations**: Interacts with `FileRepository` to fetch the list of files to be backed up.
- **Firebase Interaction**: Uses `FirebaseDBHelper` to upload files to Firebase for backup.
- **Error Handling**: Manages errors during the backup process, including retries if a failure occurs.
- **WorkManager Integration**: Utilizes WorkManager to schedule and manage the execution of backup tasks in the background.

### FileRepository

#### Purpose

Manages data operations for files.

#### Functionality

- **Data Access**: Provides methods to fetch, save, and update file data.
- **Backup and Restore Management**: Contains logic to start and manage backup and restore operations, including interacting with Firebase.

### FirebaseDBHelper

#### Purpose

Handles Firebase database operations.

#### Functionality

- **File Upload**: Provides methods to upload files to Firebase for backup purposes.
- **Data Retrieval**: Provides methods to retrieve backup files from Firebase.
- **Firebase Integration**: Manages the connection and interactions with the Firebase Realtime Database or Firestore.

### DeviceStatus

#### Purpose

Checks and provides the status of the device.

#### Functionality

- **Network Status**: Contains methods to check if the device is connected to the internet.
- **Battery Status**: Contains methods to check if the device has sufficient battery for backup or restore operations.
- **Status Reporting**: Provides status reports to other components to make informed decisions about starting or continuing backup and restore operations.

### SensorHandler

#### Purpose

Manages sensor data (e.g., accelerometer, gyroscope).

#### Functionality

- **Sensor Management**: Provides methods to start and stop sensor data collection.
- **Data Processing**: Retrieves and processes sensor data, potentially for use in determining if backup operations should pause (e.g., if the device is moving).
- **Sensor Integration**: Manages connections to device sensors and handles data updates.
