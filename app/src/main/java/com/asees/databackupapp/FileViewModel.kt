package com.asees.databackupapp

//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//
//class FileViewModel(private val repository: FileRepository) : ViewModel() {
//
//    fun fileAccessed(file: FileEntity) {
//        viewModelScope.launch {
//            repository.updateFileAccess(file.id, System.currentTimeMillis())
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun backupFile(file: FileEntity) {
//        viewModelScope.launch {
//            if (DeviceStatusUtils.hasEnoughBattery(repository.context) &&
//                DeviceStatusUtils.isNetworkAvailable(repository.context)) {
//                FirebaseDBHelper.backupFile(
//                    file,
//                    repository.context,
//                    onSuccess = {
//                        viewModelScope.launch {
//                            try {
//                                file.isBackedUp = true
//                                repository.updateFile(file)
//                            } catch (e: Exception) {
//                                Log.e("BackupError", "Failed to update file: ${e.message}")
//                            }
//                        }
//                    },
//                    onError = { exception ->
//                        Log.e("BackupError", "Backup failed: ${exception.localizedMessage}")
//                    }
//                )
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun restoreFile(file: FileEntity) {
//        viewModelScope.launch {
//            if (DeviceStatusUtils.hasEnoughBattery(repository.context) &&
//                DeviceStatusUtils.isNetworkAvailable(repository.context)) {
//                FirebaseDBHelper.prefetchFile(
//                    file,
//                    repository.context,
//                    onSuccess = {
//                        viewModelScope.launch {
//                            try {
//                                file.isBackedUp = false
//                                repository.updateFile(file)
//                            } catch (e: Exception) {
//                                Log.e("RestoreError", "Failed to update file: ${e.message}")
//                            }
//                        }
//                    },
//                    onError = { exception ->
//                        Log.e("RestoreError", "Restore failed: ${exception.localizedMessage}")
//                    }
//                )
//            }
//        }
//    }
//}
