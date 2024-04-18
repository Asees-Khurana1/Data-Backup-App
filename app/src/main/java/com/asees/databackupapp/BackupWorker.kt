package com.asees.databackupapp

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class BackupWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun doWork(): Result {
        if (!DeviceStatusUtils.hasEnoughBattery(applicationContext) || !DeviceStatusUtils.isNetworkAvailable(applicationContext)) {
            return Result.retry()
        }

        val database = AppDatabase.getDatabase(applicationContext)
        val files = database.fileDao().getFilesForBackup(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30))
        files.forEach { file ->
            FirebaseDBHelper.backupFile(file)
            file.isBackedUp = true
            database.fileDao().updateFile(file)
        }
        return Result.success()
    }
}

