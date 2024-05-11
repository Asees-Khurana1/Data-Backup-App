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
    override suspend fun doWork(): Result {
        val repository = FileRepository(applicationContext)

        // Ensure we're in an IO dispatcher for database and network operations
        return withContext(Dispatchers.IO) {
            // Check device conditions before proceeding
            if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
                Result.retry()
            } else {
                val filesToBackup = repository.getFilesForBackup(System.currentTimeMillis() - THIRTY_DAYS)

                filesToBackup.forEach { file ->
                    try {
                        // Ensure network and storage interaction is wrapped correctly
                        FirebaseDBHelper.backupFile(
                            file,
                            onSuccess = {
                                // Switch to IO dispatcher to update database
                                launch(Dispatchers.IO) {
                                    try {
                                        repository.updateFile(file.apply { isBackedUp = true })
                                    } catch (e: Exception) {
                                        println("Error updating database: ${e.localizedMessage}")
                                    }
                                }
                            },
                            onError = { exception ->
                                println("Error backing up file: ${exception.localizedMessage}")
                            }
                        )
                    } catch (e: Exception) {
                        println("Exception during backup operation: ${e.localizedMessage}")
                    }
                }

                Result.success()
            }
        }
    }

    companion object {
        private const val THIRTY_DAYS = 2592000000L // 30 days in milliseconds
    }
}
