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

        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return@withContext Result.retry()
        }

        try {
            val filesToRestore = repository.getFilesToRestore()
            filesToRestore.forEach { file ->
                FirebaseDBHelper.prefetchFile(
                    file,
                    onProgress = { progress ->
                        // Progress can be logged or shown in a notification
                    },
                    onSuccess = {
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
            }
            Result.success()
        } catch (e: Exception) {
            println("Restore worker failed: ${e.localizedMessage}")
            Result.failure()
        }
    }
}
