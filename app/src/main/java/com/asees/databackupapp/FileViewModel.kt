package com.asees.databackupapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FileViewModel(private val repository: FileRepository) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.M)
    fun backupFile(file: FileEntity) {
        viewModelScope.launch {
            if (DeviceStatusUtils.hasEnoughBattery(repository.context) &&
                DeviceStatusUtils.isNetworkAvailable(repository.context)) {
                FirebaseDBHelper.backupFile(
                    file,
                    onSuccess = {
                        viewModelScope.launch {
                            file.isBackedUp = true
                            repository.updateFile(file)
                        }
                    },
                    onError = { exception ->
                        // Log error or inform the user
                        println("Backup error: ${exception.message}")
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun restoreFile(file: FileEntity) {
        viewModelScope.launch {
            if (DeviceStatusUtils.hasEnoughBattery(repository.context) &&
                DeviceStatusUtils.isNetworkAvailable(repository.context)) {
                FirebaseDBHelper.prefetchFile(
                    file,
                    onSuccess = {
                        viewModelScope.launch {
                            file.isBackedUp = false
                            repository.updateFile(file)
                        }
                    },
                    onError = { exception ->
                        // Log error or inform the user
                        println("Restore error: ${exception.message}")
                    }
                )
            }
        }
    }
}
