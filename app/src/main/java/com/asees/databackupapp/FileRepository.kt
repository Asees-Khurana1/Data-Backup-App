package com.asees.databackupapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.temporal.ChronoUnit

class FileRepository(private val fileDao: FileDao) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFilesToBackup(): List<FileEntity> {
        // Calculate the timestamp for one month ago
        val oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()
        return fileDao.getFilesNotAccessedInLastMonth(oneMonthAgo)
    }


    fun updateFileAccess(file: FileEntity) {
        fileDao.update(file)
    }
}