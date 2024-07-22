# DataBackupApp

DataBackupApp is an Android application designed to backup and restore data efficiently. It leverages Jetpack Compose for the UI, follows the MVVM architecture, and adheres to SOLID principles and best practices from the Android Developers website.

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
  - [RestoreWorker](#restoreworker)
  - [FileViewModel](#fileviewmodel)
  - [FileRepository](#filerepository)
  - [FileDao](#filedao)
  - [FileEntity](#fileentity)
  - [Database](#database)
  - [FirebaseDBHelper](#firebasedbhelper)
  - [DeviceStatus](#devicestatus)
  - [SensorHandler](#sensorhandler)
- [License](#license)

## Features

- Backup data to Firebase
- Restore data from Firebase
- Background backup and restore operations
- File management with Room database
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

The application follows the MVVM (Model-View-ViewModel) architecture pattern to ensure a clean separation of concerns and promote testability. It also uses Jetpack components such as LiveData, Room, and WorkManager.

## Components

### MainActivity

#### Purpose

MainActivity is the primary entry point of the application, setting up the initial UI and handling primary user interactions.

#### Functionality

- Initializes the UI defined in `activity_main.xml`
- Navigates to `BackupActivity` and `RestoreActivity`

### BackupActivity

#### Purpose

Handles the backup operations of the app.

#### Functionality

- Initiates backup processes
- Interacts with `FileViewModel` to manage file data
- Displays backup status and progress

### RestoreActivity

#### Purpose

Manages the restoration of data from backup.

#### Functionality

- Initiates the restore process
- Fetches data from backup storage
- Updates UI with restore status and progress

### BackupService

#### Purpose

A service that handles background backup tasks.

#### Functionality

- Runs backup operations in the background to avoid blocking the main UI
- Utilizes `BackupWorker` for backup tasks

### BackupWorker

#### Purpose

Executes the backup operations.

#### Functionality

- Interacts with `FileRepository` to fetch files to be backed up
- Uses `FirebaseDBHelper` to store backup data in Firebase
- Handles errors and retries

### RestoreWorker

#### Purpose

Executes the restoration operations.

#### Functionality

- Retrieves backup data from Firebase
- Uses `FileRepository` to restore files
- Manages errors and retries

### FileViewModel

#### Purpose

Provides data and handles business logic for file-related operations.

#### Functionality

- Exposes live data for files to the UI
- Uses `FileRepository` to interact with data sources

### FileRepository

#### Purpose

Manages data operations for files.

#### Functionality

- Provides methods to fetch, save, and update file data
- Interacts with `FileDao` for database operations

### FileDao

#### Purpose

Defines database operations for file entities.

#### Functionality

- Contains methods annotated with Room Database annotations to perform CRUD operations on `FileEntity`

### FileEntity

#### Purpose

Represents a file entity in the database.

#### Functionality

- Defines the structure of file data stored in the database

### Database

#### Purpose

Manages the Room Database instance.

#### Functionality

- Provides access to DAOs such as `FileDao`

### FirebaseDBHelper

#### Purpose

Handles Firebase database operations.

#### Functionality

- Provides methods to store and retrieve data from Firebase

### DeviceStatus

#### Purpose

Checks and provides the status of the device.

#### Functionality

- Methods to check network connectivity, battery status, etc.

### SensorHandler

#### Purpose

Manages sensor data (e.g., accelerometer, gyroscope).

#### Functionality

- Provides methods to start and stop sensor data collection
- Retrieves and processes sensor data

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
