package com.asees.databackupapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileRepository(val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val fileDao = database.fileDao()

    suspend fun updateFile(file: FileEntity) = withContext(Dispatchers.IO) {
        fileDao.updateFile(file)
    }

    suspend fun getFilesForBackup(threshold: Long): List<FileEntity> {
        return fileDao.getFilesForBackup(threshold)
    }

    suspend fun getFilesToRestore(): List<FileEntity> {
        return fileDao.getFilesToRestore()
    }

}
