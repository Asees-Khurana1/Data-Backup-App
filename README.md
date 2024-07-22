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
- **ViewModel Integration**: Interacts with `FileViewModel` to observe backup status and update the UI accordingly.
- **Progress Display**: Updates the user interface with the progress and status of the backup operation using LiveData observers.

### RestoreActivity

#### Purpose

Manages the restoration of data from backup.

#### Functionality

- **UI Setup**: Sets the content view using the layout file `activity_restore.xml`.
- **Restore Initialization**: Starts the restore process when the user clicks the restore button.
- **ViewModel Integration**: Interacts with `FileViewModel` to observe restore status and update the UI accordingly.
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

### RestoreWorker

#### Purpose

Executes the restoration operations.

#### Functionality

- **Data Retrieval**: Fetches backup data from Firebase using `FirebaseDBHelper`.
- **File Restoration**: Uses `FileRepository` to restore files to their original locations on the device.
- **Error Handling**: Manages errors during the restore process, including retries if a failure occurs.
- **WorkManager Integration**: Utilizes WorkManager to schedule and manage the execution of restore tasks in the background.

### FileViewModel

#### Purpose

Provides data and handles business logic for file-related operations.

#### Functionality

- **LiveData Exposure**: Exposes LiveData for backup and restore statuses to the UI components.
- **Repository Integration**: Uses `FileRepository` to interact with the data layer and perform backup and restore operations.
- **Business Logic**: Contains methods to start backup and restore processes and updates LiveData objects to reflect the current status.

### FileRepository

#### Purpose

Manages data operations for files.

#### Functionality

- **Data Access**: Provides methods to fetch, save, and update file data in the database.
- **DAO Integration**: Interacts with `FileDao` to perform database operations on file entities.
- **Backup and Restore Management**: Contains logic to start and manage backup and restore operations, including interacting with Firebase.

### FileDao

#### Purpose

Defines database operations for file entities.

#### Functionality

- **CRUD Operations**: Contains methods annotated with Room Database annotations to perform Create, Read, Update, and Delete operations on `FileEntity`.
- **Query Execution**: Executes SQL queries to retrieve and manipulate file data stored in the Room database.

### FileEntity

#### Purpose

Represents a file entity in the database.

#### Functionality

- **Data Structure**: Defines the structure of file data stored in the database, including fields for file name, file path, and backup status.
- **Entity Annotations**: Uses Room annotations to specify table name and primary key.

### Database

#### Purpose

Manages the Room Database instance.

#### Functionality

- **Database Setup**: Configures and provides a singleton instance of the Room database.
- **DAO Access**: Provides access to DAOs such as `FileDao` to perform database operations.
- **Database Configuration**: Specifies the database version and entities included in the database schema.

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
