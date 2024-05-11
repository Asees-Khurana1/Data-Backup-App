package com.asees.databackupapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*

class BackupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repository = FileRepository(applicationContext)

        // Check battery and network availability before proceeding
        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return@withContext Result.retry()
        }

        // Process each file that needs to be backed up
        val filesToBackup = repository.getFilesForBackup(System.currentTimeMillis() - THIRTY_DAYS)
        filesToBackup.forEach { file ->
            try {
                FirebaseDBHelper.backupFile(
                    file,
                    onSuccess = {
                        // Ensure we are in a coroutine scope when calling suspend functions
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                repository.updateFile(file.apply { isBackedUp = true })
                            } catch (e: Exception) {
                                println("Error updating file status: ${e.localizedMessage}")
                            }
                        }
                    },
                    onError = { exception ->
                        println("Error backing up file: ${file.fileName}, ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                println("Backup process failed: ${e.localizedMessage}")
            }
        }
        Result.success()
    }

    companion object {
        private const val THIRTY_DAYS = 2592000000L // 30 days in milliseconds
    }
}
