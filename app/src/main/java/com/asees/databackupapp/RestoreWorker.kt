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
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val repository = FileRepository(applicationContext)

        // Check battery and network availability before proceeding
        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return@withContext Result.retry()
        }

        // Process each file that needs to be restored
        val filesToRestore = repository.getFilesToRestore()
        filesToRestore.forEach { file ->
            try {
                FirebaseDBHelper.prefetchFile(
                    file,
                    onSuccess = {
                        // Ensure we are in a coroutine scope when calling suspend functions
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                repository.updateFile(file.apply { isBackedUp = false })
                            } catch (e: Exception) {
                                println("Error updating file status: ${e.localizedMessage}")
                            }
                        }
                    },
                    onError = { exception ->
                        println("Error restoring file: ${file.fileName}, ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                println("Restore process failed: ${e.localizedMessage}")
            }
        }
        Result.success()
    }
}
