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

        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return@withContext Result.retry()
        }

        try {
            val filesToBackup = repository.getFilesForBackup(System.currentTimeMillis() - THIRTY_DAYS)
            filesToBackup.forEach { file ->
                FirebaseDBHelper.backupFile(
                    file,
                    onProgress = { progress ->
                        // Progress can be logged or shown in a notification
                    },
                    onSuccess = {
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
            }
            Result.success()
        } catch (e: Exception) {
            println("Backup worker failed: ${e.localizedMessage}")
            Result.failure()
        }
    }

    companion object {
        private const val THIRTY_DAYS = 2592000000L // 30 days in milliseconds
    }
}
