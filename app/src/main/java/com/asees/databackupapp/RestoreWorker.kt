package com.asees.databackupapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

class RestoreWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun doWork(): Result {
        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return Result.retry()
        }

        val database = AppDatabase.getDatabase(applicationContext)
        val files = database.fileDao().getFilesForRestore()
        files.forEach { file ->
            FirebaseDBHelper.prefetchFile(file)
            file.isBackedUp = false  // Update based on your app logic
            database.fileDao().updateFile(file)
        }
        return Result.success()
    }
}
