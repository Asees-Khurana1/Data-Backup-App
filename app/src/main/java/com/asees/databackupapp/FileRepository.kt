package com.asees.databackupapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val fileDao = database.fileDao()

    suspend fun updateFile(file: FileEntity) = withContext(Dispatchers.IO) {
        fileDao.updateFile(file)
    }

    suspend fun updateFileAccess(fileId: String, lastAccessed: Long) = withContext(Dispatchers.IO) {
        fileDao.updateFileAccess(fileId, lastAccessed)
    }

    suspend fun getFilesForBackup(threshold: Long): List<FileEntity> = withContext(Dispatchers.IO) {
        fileDao.getFilesForBackup(threshold)
    }

    suspend fun getFilesToRestore(): List<FileEntity> = withContext(Dispatchers.IO) {
        fileDao.getFilesToRestore()
    }
}
