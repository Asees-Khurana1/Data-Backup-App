package com.asees.databackupapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*

class RestoreWorker(
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
                val filesToRestore = repository.getFilesToRestore()

                filesToRestore.forEach { file ->
                    try {
                        // Ensure network and storage interaction is wrapped correctly
                        FirebaseDBHelper.prefetchFile(
                            file,
                            onSuccess = {
                                // Switch to IO dispatcher to update database
                                launch(Dispatchers.IO) {
                                    try {
                                        repository.updateFile(file.apply { isBackedUp = false })
                                    } catch (e: Exception) {
                                        println("Error updating database: ${e.localizedMessage}")
                                    }
                                }
                            },
                            onError = { exception ->
                                println("Error restoring file: ${exception.localizedMessage}")
                            }
                        )
                    } catch (e: Exception) {
                        println("Exception during restoration operation: ${e.localizedMessage}")
                    }
                }

                Result.success()
            }
        }
    }
}
